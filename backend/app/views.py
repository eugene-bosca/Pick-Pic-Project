from rest_framework import viewsets
from .models import Item, User
from .serializers import ItemSerializer, UserSerializer
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status

from .google_cloud_storage.bucket import *
from datetime import datetime

class ItemViewSet(viewsets.ModelViewSet):
    queryset = Item.objects.all()
    serializer_class = ItemSerializer

class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer

@api_view(['GET'])
def authenticate(request):
    username = request.data.get('username')
    password = request.data.get('password')

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
    
@api_view(['GET', 'POST'])
def picture(request):
    if request.method == 'GET':
        
        # get picture_id from request parameter
        picture_name = request.GET.get("picture_name")  
        file_bytes = download_from_gcs('pick-pic', picture_name)
        return Response(file_bytes)

    elif request.method == 'POST':
        
        file_bytes = request.body
        content_type = request.headers.get('Content-Type') 
        unique_name = datetime.now().strftime("%Y%m%d%H%M%S%f")

        if content_type == 'image/jpeg':
            unique_name += '.jpeg'
        elif content_type == 'image/png':
            unique_name += '.png'
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST, data={ "error": "no header - Content-Type" })

        print(unique_name)

        upload_to_gcs('pick-pic', file_bytes, unique_name, content_type)

        return Response(status=status.HTTP_204_NO_CONTENT)