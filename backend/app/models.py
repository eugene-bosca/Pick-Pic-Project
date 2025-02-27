from django.db import models
import uuid
    
class User(models.Model):
    user_id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    display_name = models.CharField(max_length=255)
    email = models.EmailField(max_length=255, unique=True)
    phone = models.CharField(max_length=30, blank=True, null=True) # Allow null/blank
    profile_picture = models.ImageField(upload_to='profile_pics') 
    # TODO: put link/endpoint filter string here instead of 'profile_pics'

    def __str__(self):
        return self.display_name
    
class UserSettings(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE, primary_key=True)
    dark_mode = models.BooleanField(default=False)

class EventOwner(models.Model):
    event = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    owner_id = models.ForeignKey(User, on_delete=models.CASCADE)

class Image(models.Model):
    image_id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    url = models.ImageField(upload_to='event_images')
    # TODO: Confirm upload_to location

class EventContent(models.Model):
    event = models.ForeignKey(EventOwner, on_delete=models.CASCADE, primary_key=True)
    event_name = models.CharField(max_length=255)
    image_id = models.ForeignKey(Image, on_delete=models.CASCADE)

class EventUser(models.Model):
    event = models.ForeignKey(EventContent, on_delete=models.CASCADE)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    
    class Meta:
        unique_together = ('event', 'user')

class ScoredBy(models.Model):
    image_id = models.ForeignKey(Image, on_delete=models.CASCADE, primary_key=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
