from rest_framework import viewsets
from .models import Item, User
from .serializers import ItemSerializer, UserSerializer
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from drf_spectacular.utils import extend_schema, OpenApiParameter, extend_schema_view, OpenApiTypes, OpenApiResponse
from django.http import FileResponse

from .google_cloud_storage.bucket import *
from datetime import datetime
import io
import mimetypes

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

@extend_schema_view(
    get=extend_schema(
        parameters=[
            OpenApiParameter("picture_name", str, OpenApiParameter.QUERY, description="picture name"),
        ],
        responses={200: OpenApiResponse(
            description="File download",
        )}
    ),
    post=extend_schema(
        request={
            "image/jpeg": OpenApiTypes.BINARY,
            "image/png": OpenApiTypes.BINARY,
        },
        responses={204: None}
    )
)    
@api_view(['GET', 'POST'])
def picture(request):
    if request.method == 'GET':
        
        picture_name = request.GET.get("picture_name")  
        print(picture_name)
        file_bytes = download_from_gcs('pick-pic', picture_name)

        file_stream = io.BytesIO(file_bytes)

        content_type, _ = mimetypes.guess_type(picture_name)
        if content_type is None:
            content_type = "application/octet-stream"

        return FileResponse(file_stream, content_type=content_type)

    elif request.method == 'POST':
        
        file_bytes = request.body
        content_type = request.headers.get('content_type') 
        unique_name = datetime.now().strftime("%Y%m%d%H%M%S%f")

        if content_type == 'image/jpeg':
            unique_name += '.jpeg'
        elif content_type == 'image/png':
            unique_name += '.png'
        upload_to_gcs('pick-pic', file_bytes, unique_name, content_type)

        return Response(status=status.HTTP_204_NO_CONTENT)