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
    event_name = models.CharField(max_length=255)
    owner = models.ForeignKey(User, on_delete=models.CASCADE)

class Image(models.Model):
    image_id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    file_name = models.CharField(max_length=50, blank=True)

class EventContent(models.Model):
    event = models.ForeignKey(Event, on_delete=models.CASCADE, primary_key=True)
    image_id = models.ForeignKey(Image, on_delete=models.CASCADE)

class EventUser(models.Model):
    event = models.ForeignKey(EventContent, on_delete=models.CASCADE)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    
    class Meta:
        unique_together = ('event', 'user')

class ScoredBy(models.Model):
    image_id = models.ForeignKey(Image, on_delete=models.CASCADE, primary_key=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
