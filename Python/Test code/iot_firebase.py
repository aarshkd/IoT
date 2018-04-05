import datetime
import firebase
import time
#import jason

fb = firebase.firebase.FirebaseApplication('https://fir-project-3c3bc.firebaseio.com/', None);

# for i in range(1,20):
#     print datetime.datetime.now()
#     fb.post('/Time', datetime.datetime.now())
#     time.sleep(1)

result = fb.get('/Time', None)
z= result.values()
print z[1]