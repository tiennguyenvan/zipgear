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

1. Click .env on the root folder with the following format
   ```env
   VUE_APP_SENDGRID_API_KEY=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
VUE_APP_SENDGRID_FROM_EMAIL=contact@sneeit.com
VUE_APP_ADMIN_EMAIL=nguyentien.jobs@gmail.com
VUE_APP_IS_DEVELOPING=false
VUE_APP_VALIDATION_CODE_LENGTH=8
VUE_APP_API_BASE_URL=http://localhost:8080/api
VUE_APP_SERVER_URL=http://localhost:8080
   ```
2. Link the file to backend:

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
   mklink .env ..\.env
   mklink .env.development.local ..\.env
   mklink .env.test.local ..\.env
   mklink .env.production.local ..\.env
   ```

## Start Backend and Frontend

1. **Backend Setup:**

   1. Navigate to the `backend/` folder.
   2. Install Maven
      1. Open Windows Powershell terminal as Administrator
      2. Install Chocolatey
      ```bash
      Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
      ```
      3. Once finished, close the terminal and open it again
      4. Install Maven
      ```bash
      choco install maven
      ```
      5. Once finished, open Visual Studio Code and check whether Maven has been installed successfully
      ```bash
      mvn -v
      ```
   3. Open a terminal on Visual Studio Code and run:

      ```bash

      mvn spring-boot:run
      ```

   4. The backend server will run on `http://localhost:8080`.

2. **Frontend Setup:**
   1. Navigate to the `frontend/` folder.
   2. Open a terminal on Visual Studio Code and run:
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
