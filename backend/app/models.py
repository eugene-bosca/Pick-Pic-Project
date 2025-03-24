from django.db import models
import uuid
from datetime import timedelta
from django.utils import timezone
from django.core.exceptions import ValidationError

class User(models.Model):
    user_id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    firebase_id = models.CharField(max_length=255, unique=True)
    display_name = models.CharField(max_length=255)
    email = models.EmailField(max_length=255, unique=True)
    phone = models.CharField(max_length=30, blank=True, null=True) 
    profile_picture = models.CharField(max_length=50, blank=True, null=True)
    
class UserSettings(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE, primary_key=True)
    dark_mode = models.BooleanField(default=False)

class Event(models.Model):
    event_id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    event_name = models.CharField(max_length=255)
    owner = models.ForeignKey(User, on_delete=models.CASCADE)
    last_modified = models.DateTimeField(auto_now=True)

class EventInvite(models.Model):
    event = models.ForeignKey(Event, editable=False, on_delete=models.CASCADE)
    creator = models.ForeignKey(User, editable=False, on_delete=models.CASCADE)
    link = models.CharField(max_length=30, default='/' ,blank=True, null=True, unique=True)
    # URLField will validate if its a proper url: www.google.ca, this stores endpoint
    expiration_date = models.DateTimeField(default=timezone.now() + timedelta(hours=24))
    # default is 24 hours

    # ensure invite link expires within 365 days
    def maxExpire(self):
        if self.expiration_date > timezone.now() + timedelta(days=365):
            raise ValidationError("The invite link can last at most 365 days.")
        
    def save(self, *args, **kwargs):
        self.maxExpire()
        super().save(*args, **kwargs)


class DirectInvite(models.Model):  # only pending invites, once accepted entry removed
    event = models.ForeignKey(Event, on_delete=models.CASCADE)
    inviter = models.ForeignKey(User, related_name='sent_invites', on_delete=models.CASCADE)
    invitee = models.ForeignKey(User, related_name='received_invites', on_delete=models.CASCADE)

    class Meta:
        unique_together = ('event', 'inviter', 'invitee')

    # ensure inviter != invitee
    def clean(self):
        if self.inviter == self.invitee:
            raise ValidationError("The inviter and invitee cannot be the same person.")

    def save(self, *args, **kwargs):
        self.clean()
        super().save(*args, **kwargs)

class Image(models.Model):
    image_id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    owner = models.ForeignKey(User, on_delete=models.CASCADE)
    file_name = models.CharField(max_length=50, blank=True)
    score = models.IntegerField(default=0)

class EventContent(models.Model):
    event = models.ForeignKey(Event, on_delete=models.CASCADE)
    image = models.ForeignKey(Image, on_delete=models.CASCADE)

    class Meta:
        unique_together = ('event', 'image')

class EventUser(models.Model):
    event = models.ForeignKey(Event, on_delete=models.CASCADE)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    
    class Meta:
        unique_together = ('event', 'user')

class ScoredBy(models.Model):
    image = models.ForeignKey(Image, on_delete=models.CASCADE)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    score = models.SmallIntegerField(default=0)

    class Meta:
        unique_together = ('image', 'user')
