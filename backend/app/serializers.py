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
    owner = serializers.PrimaryKeyRelatedField(queryset=User.objects.all(), write_only=True)  # Use ID for POST

    class Meta:
        model = Event
        fields = ['event_id', 'event_name', 'owner']

    def to_representation(self, instance):
        # Customize GET response to use UserSerializer instead of just the user ID."""
        data = super().to_representation(instance)
        data['owner'] = UserSerializer(instance.owner).data  # Serialize owner details for GET
        return data

    def create(self, validated_data):
        owner_uuid = validated_data.pop('owner')
        event_name = validated_data.pop('event_name')

        if Event.objects.filter(owner=owner_uuid, event_name=event_name).exists():
            raise serializers.ValidationError({"Validation Error": "An event with this name already exists for the owner."})

        return Event.objects.create(owner=owner_uuid, event_name=event_name, **validated_data)

class EventUserSerializer(serializers.ModelSerializer):
    event = EventSerializer()
    user = UserSerializer()

    class Meta:
        model = EventUser
        fields = "__all__"

class ImageSerializer(serializers.ModelSerializer):
    class Meta:
        model = Image
        fields = "__all__"


class EventContentSerializer(serializers.ModelSerializer):
    event = EventSerializer()
    image = ImageSerializer()

    class Meta:
        model = EventContent
        fields = "__all__"


class ScoredBySerializer(serializers.ModelSerializer):
    image = ImageSerializer()
    user = UserSerializer()

    class Meta:
        model = ScoredBy
        fields = "__all__"
