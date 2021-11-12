from django.db import models

class User(models.Model):
    uid = models.CharField(primary_key=True, max_length=20)

class Orders(models.Model):
    oid = models.CharField(primary_key=True, max_length=200)
    state = models.IntegerField(default=0)
    date = models.DateTimeField('date added')

class UserOrder(models.Model):
    uid = models.ForeignKey(User, on_delete=models.CASCADE)
    oid = models.ForeignKey(Orders, on_delete=models.CASCADE)