from django.http import HttpResponse
from django.views.decorators.http import require_http_methods
from datetime import datetime
import json
from .models import User, Orders, UserOrder

@require_http_methods(['GET', 'POST'])
def unameAgent(request):
    if request.method == 'GET':
        username = request.GET.get('uname').encode('UTF-8').decode()
        print(username)
        found = False
        for user in User.objects.all():
            if username == user.uid:
                found = True
                break
        if found:
            return HttpResponse('unameAgent get found')
        return HttpResponse('unameAgent get not found', status=404)
    elif request.method == 'POST':
        body_unicode = request.body.decode('UTF-8')
        print(body_unicode)
        values = body_unicode.split('&')
        body = dict()
        for v in values:
            key = v.split('=')[0]
            value = v.split('=')[1]
            body[key] = value
        found = False
        for user in User.objects.all():
            if body['uname'] == user.uid:
                found = True
                break
        if not found:
            new_u = User(uid=body['uname'])
            new_u.save()
            return HttpResponse('registered new user')
        return HttpResponse('user already exist', status=500)

@require_http_methods(['GET', 'POST'])
def orderAgent(request):
    if request.method == 'GET':
        username = request.GET.get('uname').encode('UTF-8').decode()
        orders = dict()
        for uo in UserOrder.objects.all():
            if username == uo.uid.uid:
                orders[uo.oid.oid] = "temp"
        for order in Orders.objects.all():
            if order.oid in orders.keys():
                orders[order.oid] = {'state': order.state, 'date': order.date.timestamp()}
        print(orders)
        return HttpResponse(json.dumps(orders))
    elif request.method == 'POST':
        body_unicode = request.body.decode('UTF-8')
        print(body_unicode)
        values = body_unicode.split('&')
        body = dict()
        for v in values:
            key = v.split('=')[0]
            value = v.split('=')[1]
            body[key] = value
        user = None
        for u in User.objects.all():
            if body['uname'] == u.uid:
                user = u
                break
        if user is not None:
            for key in body.keys():
                if key != 'uname':
                    new_order = Orders(oid=body[key], state=0, date=datetime.now())
                    new_order.save()
                    new_uo = UserOrder(uid=user, oid=new_order)
                    new_uo.save()
            return HttpResponse('added orders')
        return HttpResponse('user not found in system', status=404)

@require_http_methods(['GET'])
def deleteAgent(request):
    if request.method == 'GET':
        username = request.GET.get('uname').encode('UTF-8').decode()
        orderid = request.GET.get('orderid').encode('UTF-8').decode()
        for uo in UserOrder.objects.all():
            if username == uo.uid.uid and orderid == uo.oid.oid:
                ## delete object
                uo.delete()
        for order in Orders.objects.all():
            if order.oid == orderid:
                order.delete()
        return HttpResponse("", status=200)

@require_http_methods(['POST'])
def updateAgent(request):
    if request.method == 'POST':
        body_unicode = request.body.decode('UTF-8')
        print(body_unicode)
        values = body_unicode.split('&')
        body = dict()
        for v in values:
            key = v.split('=')[0]
            value = v.split('=')[1]
            body[key] = value
        user = None
        for u in User.objects.all():
            if body['uname'] == u.uid:
                user = u
                break
        if user is not None:
            if 'orderid' not in body.keys():
                return HttpResponse('failure to unmarshall, no orderid given.', status=404)
            else:
                idFound = False
                for uo in UserOrder.objects.all():
                    if body['orderid'] == uo.oid.oid:
                        idFound = True
                if not idFound:
                    return HttpResponse('order not found in system', status=404)
                else:
                    for order in Orders.objects.all():
                        if order.oid == body['orderid']:
                            order.state = body['status']
                            order.save()
            return HttpResponse('deleted orders')
        return HttpResponse('user not found in system', status=404)