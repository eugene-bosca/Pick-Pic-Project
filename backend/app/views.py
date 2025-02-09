from rest_framework import viewsets
from .models import Item, User
from .serializers import ItemSerializer, UserSerializer
from rest_framework.decorators import api_view
from rest_framework.response import Response
from django.contrib.auth.hashers import check_password


class ItemViewSet(viewsets.ModelViewSet):
    queryset = Item.objects.all()
    serializer_class = ItemSerializer

class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer

@api_view(['GET'])
def authenticate(request):
    username = request.query_params.get('username')
    password = request.query_params.get('password')

    if not username or not password:
        return Response({"error": "Username and password are required"}, status=400)

    try:
        user = User.objects.get(username=username)
        if user.password == password:
            return Response({"exists": True, "comment": "N/A"})
        else:
            return Response({"exists": False, "comment": "incorrect password"})
    except User.DoesNotExist:
        return Response({"exists": False, "comment": "user does not exist"})
    except User.MultipleObjectsReturned:
        return Response({"exists": False, "comment": "multiple users with the same username exists???"})
