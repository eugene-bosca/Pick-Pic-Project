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
from drf_spectacular.views import SpectacularAPIView, SpectacularSwaggerView
from rest_framework_nested.routers import NestedDefaultRouter

from . import views

router = DefaultRouter()
router.register(r'user', views.UserViewSet)
router.register(r'user_settings', views.UserSettingsViewSet)
# router.register(r'event', views.EventViewSet)
router.register(r'event_users', views.EventUserViewSet)
# router.register(r'images', views.ImageViewSet)
# router.register(r'event/content', views.EventContentViewSet)
router.register(r'scored_by', views.ScoredByViewSet)



urlpatterns = [
    # swagger
    path('schema/', SpectacularAPIView.as_view(), name='schema'),
    path('swagger/', SpectacularSwaggerView.as_view(url_name='schema'), name='swagger-ui'),

    # custom CRUD endpoints
    path('', include(router.urls)),

    # image/picture endpoints
    path('event/<str:event_id>/image/', views.create_image, name='PUT Image'),
    path('event/<str:event_id>/image/<str:image_id>/', views.get_delete_image, name='GET/DELETE Image'),
    path('event/<str:event_id>/image/count/', views.event_image_count, name='event_image_count'),
    path('event/<str:event_id>/image/highest_score/', views.get_highest_scored_image, name='get_highest_scored_image'),

    # event
    path('event/create/', views.create_new_event, name='Create New Event'),
    path('event/<str:event_id>/', views.event_info, name='event info'),
    path('event/<str:event_id>/users', views.EventUserViewSet.as_view({'get': 'retrieve'}), name='Event Users'),
    path('event/<str:event_id>/content/', views.EventContentViewSet.as_view({'get': 'retrieve'}), name='Event Content'),
    path('event/<str:event_id>/invite/user/', views.invite_to_event, name='invite user(s) to event'),
    path('event/invite/link/decode/<str:invite_link>/', views.resolve_invite_link, name='resolve_invite_link'),
    path('event/<str:event_id>/last_modified/', views.event_last_modified, name='Event Last Modified Timestamp'),

    # invite
    path('event/<uuid:event_id>/invite/link/', views.generate_invite_link, name='generate_invite_link'),

    # these two can combine into one /event/<event-Id>/invite/ and have the accept/decide
    path('event/<str:event_id>/invite/<str:invite_link>/accept/', views.accept_event_user, name='add_event_user'),
    path('event/<str:event_id>/invite/<str:invite_link>/decline/', views.remove_event_user, name='remove_event_user'),

    path('user/<uuid:user_id>/pending_events_full/', views.get_pending_events, name='get_pending_events'),
    path('user/<str:user_id>/events/', views.list_users_events, name='List Users Events'),

    path('user/from_fire_base/<str:firebase_id>/', views.get_user_id_by_firebase_id, name='Exchange User ID For Firebase ID'),
    path('user/from_email/<str:email>/', views.get_user_id_from_email, name='get user id from email'),
]