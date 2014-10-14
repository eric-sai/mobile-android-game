Read Me
******************************************
This is a description file for the programming project-subject COMP90018.

Android application part.
******************************************

1. Team information:

	Team Id: 4
	Team Members: Xiaoxiao Ma, Jun Han, Kuai Yu, Yitian Zhang.

2. Introduction to the APP part of the project.

2.1 explains which parts of the code does what for the project
	
	2.1.1 package - com.unimelb.mobile.breakout.server.po
		
		In this package a class - User is implemented to create instances of players.
	
	2.1.2 package - com.unimelb.mobile.breakoutgame
		
		2.1.2.1 Ball.java:
			
			This class create a instance of the ball, collision detect is implemented in this part.
		
		2.1.2.2 BreakOutMain.java:
			
			This class creates the main page after user login, include start and other buttons. Jump to corresponding activity with specific button pressed.
			
		2.1.2.3 CannonGame.java:
			
			This class is the main activity that displays the main User interface of the game, include the CannonView and game information.
			
		2.1.2.4 CannonView.java:
		
			This is a surfaceview that displays different elements involved in this game, the ball, pad and target bricks.
			
		2.1.2.5 FileDownProcessBarActivity.java:
			
			This activity class implements the level file download function.
			
		2.1.2.6 GetToptenThread.java:
			
			This class creates a thread that get the top ten players stored in the server.
		
		2.1.2.7 Help.java:
			
			This activity displays the help video that help players to play with the game.
			
		2.1.2.8 Line.java:
			
			This class is used to create instances of target lines.
			
		2.1.2.9 LocalGame.java:
			
			This class creates the user interface that enables user to input the level they want to start play with.
			
		2.1.2.10 Login.java:
			
			This class creates the login user interface and implement the login function.
		
		2.1.2.11 Regist.java:
			
			This class creates the regist UI. regist new users in this page.
			
		2.1.2.12 SendResultThread.java:
			
			This class creates a thread that send current user record to the server.
			
		2.1.2.13 Topten.java:
		
			This class displays the top ten players.
			
	2.1.3 res
		
		All the media and layout files are included in this folder.
		
	2.1.4 assets
		
		All the level files are under this folder.

2.2 indicates which team member is responsible for which part of the project.
	
		All the team members are developing the app and server together.

