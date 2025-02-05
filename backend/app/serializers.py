from rest_framework import serializers, filters
from .models import Item, User

class ItemSerializer(serializers.ModelSerializer):
    class Meta:
        model = Item
        fields = '__all__'

class UserSerializer(serializers.ModelSerializer):
    class User:
        model = User
        fields = '__all__'
        filter_backends = [filters.SearchFilter]
        search_fields = ['username', 'email']