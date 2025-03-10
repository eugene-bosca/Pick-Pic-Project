from django.db.models.signals import post_save, post_delete
from django.dispatch import receiver
from .models import EventContent, Event

# Signal to update last_modified whenever EventContent is modified
@receiver(post_save, sender=EventContent)
@receiver(post_delete, sender=EventContent)
def update_event_last_modified(sender, instance, **kwargs):
    event = instance.event  # Get the related event
    event.save()  # Triggers auto_now=True on last_modified