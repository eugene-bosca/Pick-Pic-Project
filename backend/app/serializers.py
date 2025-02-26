from rest_framework import serializers, filters
from .models import User

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = '__all__'
        filter_backends = [filters.SearchFilter]
        search_fields = ['username', 'email']