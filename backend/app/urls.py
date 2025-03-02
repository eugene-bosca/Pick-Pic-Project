"""
URL configuration for PickPic project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""

from django.contrib import admin
from django.urls import path, include
from rest_framework.routers import DefaultRouter
from app.views import authenticate, picture
from drf_spectacular.views import SpectacularAPIView, SpectacularSwaggerView
from . import views

router = DefaultRouter()
router.register(r'users', views.UserViewSet)
router.register(r'user-settings', views.UserSettingsViewSet)
router.register(r'event', views.EventOwnerViewSet)
router.register(r'event-users', views.EventUserViewSet)
router.register(r'images', views.ImageViewSet)
router.register(r'event-contents', views.EventContentViewSet)
router.register(r'scored-by', views.ScoredByViewSet)
urlpatterns = [
    # swagger
    path('schema/', SpectacularAPIView.as_view(), name='schema'),
    path('swagger/', SpectacularSwaggerView.as_view(url_name='schema'), name='swagger-ui'),

    # admin
    path('admin/', admin.site.urls),

    # custom CRUD endpoints
    path('', include(router.urls)),

    # custom endpoints
    path('authenticate/', views.authenticate, name='authenticate'),

    path('picture/', views.picture, name='picture'),
    path('event/image-count/', views.event_image_count, name='event_image_count'),

    path('list-users-events/', views.list_users_events, name='list_users_events'),
]