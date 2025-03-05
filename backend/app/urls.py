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
router.register(r'user_settings', views.UserSettingsViewSet)
router.register(r'event', views.EventViewSet)
router.register(r'event_users', views.EventUserViewSet)
router.register(r'images', views.ImageViewSet)
router.register(r'event_contents', views.EventContentViewSet)
router.register(r'scored_by', views.ScoredByViewSet)
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
    path('event/<uuid:event_id>/highest_scored_image/', views.get_highest_scored_image, name='get_highest_scored_image'),


    path('event/image-count/<uuid:event_id>/', views.event_image_count, name='event_image_count'),
    path('list-users-events/<uuid:user_id>/', views.list_users_events, name='list_users_events'),

    # invite related
    path('invite_to_event/', views.invite_to_event, name='invite user to event'),
    path('generate_invite_link/<uuid:event_id>/', views.generate_invite_link, name='generate_invite_link'),
    path('resolve_invite_link/<str:encoded_event_id>/', views.resolve_invite_link, name='resolve_invite_link'),
    path('add_user_to_event/<uuid:event_id>/<uuid:user_id>/', views.add_user_to_event, name='add_user_to_event'),

    path('event/image-count/<str:event_id>/', views.event_image_count, name='event_image_count'),
    path('list_users_events/<str:user_id>/', views.list_users_events, name='List Users Events'),
    path('get_user_id_by_firebase_id/<str:firebase_id>/', views.get_user_id_by_firebase_id, name='exchange_user_id_for_firebase_id'),
    path('create_new_event/', views.create_new_event, name='create new event'),
    path('get_user_id_from_email/<str:email>/', views.get_user_id_from_email, name='get user id from email'),
    path('event/<uuid:event_id>/highest_scored_image/', views.get_highest_scored_image, name='get_highest_scored_image'),

    # pending invites
    path('events/<uuid:event_id>/users/<uuid:user_id>/remove/', views.remove_event_user, name='remove_event_user'),
    path('events/<uuid:event_id>/users/<uuid:user_id>/accept/', views.accept_event_user, name='accept_event_user'),
]