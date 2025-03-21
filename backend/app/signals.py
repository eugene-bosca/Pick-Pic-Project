from django.db.models.signals import post_save, post_delete
from django.dispatch import receiver
from .models import EventContent, ScoredBy
from django.db.models import Sum

@receiver(post_save, sender=ScoredBy)
@receiver(post_delete, sender=ScoredBy)
def update_scored_by_total_vote(sender, instance:ScoredBy, **kwargs):
    scoredBy = instance
    image = scoredBy.image
    image.score = ScoredBy.objects.filter(image_id=image.image_id).aggregate(total=Sum('score'))['total'] or 0
    image.save()