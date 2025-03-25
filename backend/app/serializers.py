from rest_framework import serializers
from .models import *

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = "__all__"

class ClientSideUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ["user_id", "display_name", "email"]

class UserSettingsSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserSettings
        fields = "__all__"

class EventSerializer(serializers.ModelSerializer):
    owner = ClientSideUserSerializer()

    class Meta:
        model = Event
        fields = "__all__"

class EventInviteSerializer(serializers.ModelSerializer):
    class Meta:
        model = EventInvite
        fields = "__all__"

class DirectInviteSerializer(serializers.ModelSerializer):
    event = EventSerializer()
    class Meta:
        model = DirectInvite
        fields = "__all__"

class SelfPendingInviteSerializer(serializers.ModelSerializer):
    event = EventSerializer()
    class Meta:
        model = DirectInvite
        fields = ["event"]

class EventUserSerializer(serializers.ModelSerializer):
    user = UserSerializer()

    class Meta:
        model = EventUser
        fields = ["user"]

    def to_representation(self, instance):
        return super().to_representation(instance)["user"]

class ImageSerializer(serializers.ModelSerializer):
    owner = UserSerializer()
    class Meta:
        model = Image
        fields = "__all__"


class EventContentSerializer(serializers.ModelSerializer):
    #event = EventSerializer()
    image = ImageSerializer()

    class Meta:
        model = EventContent
        fields = ["image"]


class ScoredBySerializer(serializers.ModelSerializer):
    image = ImageSerializer()
    user = ClientSideUserSerializer()

    class Meta:
        model = ScoredBy
        fields = "__all__"

class VoteImageSerializer(serializers.Serializer):
    user_id = serializers.UUIDField()
    vote = serializers.CharField()

class EmailSerializer(serializers.Serializer):
    emails = serializers.ListField(
        child=serializers.CharField(),
    )

class UserIDSerializer(serializers.Serializer):
    user_id = serializers.UUIDField()

class UserListSerializer(serializers.Serializer):
    user_ids = serializers.ListField(
        child=serializers.UUIDField(),
        allow_empty=False
    )