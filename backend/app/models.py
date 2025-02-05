from django.db import models

class Item(models.Model):
    name = models.CharField(max_length=255)

    def __str__(self):
        return self.name
    
class User(models.Model):
    username = models.CharField(max_length=255)
    email = models.CharField(max_length=255)
    password = models.CharField(max_length=255)

    def __str__(self):
        return self.username