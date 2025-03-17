from rest_framework import serializers
from .models import User, UserSettings, Event, EventUser, Image, EventContent, ScoredBy

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = "__all__"

class UserSettingsSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserSettings
        fields = "__all__"


class EventSerializer(serializers.ModelSerializer):
    #owner = UserSerializer()

    class Meta:
        model = Event
        fields = "__all__"

class EventUserSerializer(serializers.ModelSerializer):
    #event = EventSerializer()
    user = UserSerializer()

    class Meta:
        model = EventUser
        fields = ["user", "accepted"]

class ImageSerializer(serializers.ModelSerializer):
    class Meta:
        model = Image
        fields = "__all__"


class EventContentSerializer(serializers.ModelSerializer):
    #event = EventSerializer()
    image = ImageSerializer()

    class Meta:
        model = EventContent
        fields = ["id", "image"]


class ScoredBySerializer(serializers.ModelSerializer):
    image = ImageSerializer()
    user = UserSerializer()

    class Meta:
        model = ScoredBy
        fields = "__all__"

class VoteImageSerializer(serializers.Serializer):
    user_id = serializers.UUIDField()
    vote = serializers.CharField()