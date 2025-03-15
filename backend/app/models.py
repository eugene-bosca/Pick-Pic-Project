from django.db import models
import uuid
    
class User(models.Model):
    user_id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    firebase_id = models.CharField(max_length=255, unique=True)
    display_name = models.CharField(max_length=255)
    email = models.EmailField(max_length=255, unique=True)
    phone = models.CharField(max_length=30, blank=True, null=True) 
    profile_picture = models.CharField(max_length=50, blank=True, null=True)

    def __str__(self):
        return self.display_name
    
class UserSettings(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE, primary_key=True)
    dark_mode = models.BooleanField(default=False)

class Event(models.Model):
    event_id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    obfuscated_event_id = models.UUIDField(default=uuid.uuid4, editable=False)
    event_name = models.CharField(max_length=255)
    owner = models.ForeignKey(User, on_delete=models.CASCADE)
    last_modified = models.DateTimeField(auto_now=True)

    def save(self, *args, **kwargs):
        if not self.obfuscated_event_id:  # Only generate if it's a new event
            self.obfuscated_event_id = uuid.uuid4()
        super().save(*args, **kwargs)

class Image(models.Model):
    image_id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    file_name = models.CharField(max_length=50, blank=True)
    score = models.IntegerField(default=0)

class EventContent(models.Model):
    event = models.ForeignKey(Event, on_delete=models.CASCADE)
    image = models.ForeignKey(Image, on_delete=models.CASCADE)

class EventUser(models.Model):
    event = models.ForeignKey(Event, on_delete=models.CASCADE)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    accepted = models.BooleanField(default=False)
    
    class Meta:
        unique_together = ('event', 'user')

class ScoredBy(models.Model):
    image = models.ForeignKey(Image, on_delete=models.CASCADE)
    user = models.ForeignKey(User, on_delete=models.CASCADE)

    class Meta:
        unique_together = ('image', 'user')
