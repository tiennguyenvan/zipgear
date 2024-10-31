# Setup Instructions for Local Development with GitHub Desktop

## Initial Setup
1. **Clone the Repository:**
   1. Open GitHub Desktop.
   2. Click **File > Clone Repository**.
   3. Select **zipgear** repository from the list.
   4. Choose a local path and click **Clone**.
2. Switching current branch:
   1. In GitHub Desktop, click **Current Branch > <branch>**.
   2. It is usually named as: `sprint-<number>`, ex: `sprint-01`.

## Setup Environment
1. Create a file name **.env** in the root folder with the following details
	 ```env
	 VUE_APP_SENDGRID_API_KEY=___SETUP_API_KEY_BY_YOURSELF___
	 VUE_APP_SENDGRID_FROM_EMAIL=contact@sneeit.com
	 VUE_APP_ADMIN_EMAIL=nguyentien.jobs@gmail.com
	 VUE_APP_SKIP_SENDING_LOGIN_EMAIL=true
	 VUE_APP_VALIDATION_CODE_LENGTH=8
	 ```
2. Link the file to backend:
<<<<<<< Updated upstream
   1. MAC
	  ```bash
      cd backend/
	  ln -s ../.env .env      
      ```
	2. Windows
	  ```bash
      cd backend/
	  fsutil behavior set symlinkEvaluation R2R:1 R2L:1 L2R:1 L2L:1
	  mklink .env ..\.env
      ```

3. Link the file to frontend:
   1. MAC
	  ```bash
      cd frontend/
	  ln -s ../.env .env      
	  ln -s ../.env .env.development.local
	  ln -s ../.env .env.test.local
	  ln -s ../.env .env.production.local
      ```
	1. Windows
	  ```bash
      cd frontend/
	  fsutil behavior set symlinkEvaluation R2R:1 R2L:1 L2R:1 L2L:1
	  ln -s ../.env .env      
	  ln -s ../.env .env.development.local
	  ln -s ../.env .env.test.local
	  ln -s ../.env .env.production.local
      ```

=======
   1. For macOS
	   ```bash
      cd backend/
	   ln -s ../.env .env
      npm run serve
      ```
   2. For Windows
      ```bash
      cd backend
      mklink .env ..\.env
      ```
3. Link the file to frontend:
   1. For macOS
	   ```bash
      cd frontend/
	   ln -s ../.env .env
      npm run serve
	   ln -s ../.env .env.development.local
	   ln -s ../.env .env.test.local
	   ln -s ../.env .env.production.local
      ```
   2. For Windows
      ```bash
      cd frontend
	   mklink .env ..\.env
      npm run serve
	   mklink .env.development.local ..\.env
	   mklink .env.test.local ..\.env 
	   mklink .env.production.local ..\.env
      ```

4. Cd to the folder (backend or front-end), check the link .env
	   ```bash
      ls -l .env	  
      ```
	  you will see something like this
	   ```bash
	   lrwxrwxrwx 1 user user 7 Oct 25 12:00 frontend/.env -> ../.env
	   ```
>>>>>>> Stashed changes
## Start Backend and Frontend
1. **Backend Setup:**
   1. Navigate to the `backend/` folder.
   2. Install 
   3. Open a terminal on Visual Studio Code and run:
      ```bash

      mvn spring-boot:run
      ```
   4. The backend server will run on `http://localhost:8080`.

2. **Frontend Setup:**
   1. Navigate to the `frontend/` folder.
   2. Open a terminal on Visual Studio Code  and run:
      ```bash
      npm install
      npm run serve
      ```
   3. The frontend will run on `http://localhost:9999`.

## Making Changes Locally
1. Make your changes and commit:
   1. Before making changes, please make sure you Pull/Fetch from the Origin
   2. Write a concise commit message.
2. Push your changes to GitHub:
   1. Click **Push origin**.

