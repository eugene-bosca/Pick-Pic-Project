from rest_framework import viewsets
from .models import User
from .serializers import UserSerializer
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from drf_spectacular.utils import extend_schema, OpenApiParameter, extend_schema_view, OpenApiTypes, OpenApiResponse

from django.http import FileResponse
from django.contrib.auth.hashers import check_password
from django.conf import settings

import jwt
from .google_cloud_storage.bucket import *
from datetime import datetime
import io
import mimetypes

class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer

# Secret key for JWT
SECRET_KEY = settings.SECRET_KEY  # Use Django's secret key

@api_view(['POST'])
def authenticate(request):
    username = request.data.get('username')
    password = request.data.get('password')

    if not username or not password:
        return Response({"error": "Username and password are required"}, status=400)

    try:
        user = User.objects.get(username=username)
        
        # Validate password securely
        if check_password(password, user.password):
            # Generate JWT token
            payload = {
                "id": user.id,
                "username": user.username,
                "exp": datetime.datetime.utcnow() + datetime.timedelta(hours=1),  # Token expires in 1 hour
                "iat": datetime.datetime.utcnow(),
            }
            token = jwt.encode(payload, SECRET_KEY, algorithm="HS256")

            return Response({
                "exists": True,
                "token": token,
                "comment": "Authentication successful"
            })
        else:
            return Response({"exists": False, "comment": "Incorrect password"}, status=401)

    except User.DoesNotExist:
        return Response({"exists": False, "comment": "User does not exist"}, status=404)
    except User.MultipleObjectsReturned:
        return Response({"exists": False, "comment": "Multiple users with the same username exist???"}, status=500)

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
        content_type = request.headers.get('Content-Type') 
        unique_name = datetime.now().strftime("%Y%m%d%H%M%S%f")

        if content_type == 'image/jpeg':
            unique_name += '.jpeg'
        elif content_type == 'image/png':
            unique_name += '.png'
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST, data={ "error": "no header - Content-Type" })

        upload_to_gcs('pick-pic', file_bytes, unique_name, content_type)

        return Response(status=status.HTTP_204_NO_CONTENT)