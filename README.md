# SmartSession

다중사용자를 위한 사용자세션

Platform : 
iOS / Android 

How to use :

 ``var smartQUser = SmartQSession.SmartQUser();
smartQUser.userId = "AAA";
smartQUser.password = SmartQ.randomNumber(length: 4)
``SmartQSession.setSmartQUser(smartQUser);
