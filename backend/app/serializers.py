from rest_framework import serializers
from .models import User, UserSettings, EventOwner, EventUser, Image, EventContent, ScoredBy

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['user_id', 'display_name', 'email', 'phone', 'profile_picture']


class UserSettingsSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserSettings
        fields = ['user', 'dark_mode']


class EventOwnerSerializer(serializers.ModelSerializer):
    owner_id = UserSerializer()

    class Meta:
        model = EventOwner
        fields = ['event', 'owner_id']


class EventUserSerializer(serializers.ModelSerializer):
    event = EventOwnerSerializer()
    user = UserSerializer()

    class Meta:
        model = EventUser
        fields = ['event', 'user']


class ImageSerializer(serializers.ModelSerializer):
    class Meta:
        model = Image
        fields = ['image_id', 'url', 'score']


class EventContentSerializer(serializers.ModelSerializer):
    event = EventOwnerSerializer()
    image_id = ImageSerializer()

    class Meta:
        model = EventContent
        fields = ['event', 'event_name', 'image_id']


class ScoredBySerializer(serializers.ModelSerializer):
    image_id = ImageSerializer()
    user = UserSerializer()

    class Meta:
        model = ScoredBy
        fields = ['image_id', 'user']