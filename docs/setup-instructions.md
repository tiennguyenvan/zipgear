# Setup Instructions for Local Development with GitHub Desktop

## Initial Setup
1. **Clone the Repository:**
   1. Open GitHub Desktop.
   2. Click **File > Clone Repository**.
   3. Select **zipgear** repository from the list.
   4. Choose a local path and click **Clone**.

2. **Backend Setup:**
   1. Navigate to the `backend/` folder.
   2. Open a terminal and run:
      ```bash
      mvn spring-boot:run
      ```
   3. The backend server will run on `http://localhost:8080`.

3. **Frontend Setup:**
   1. Navigate to the `frontend/` folder.
   2. Open a terminal and run:
      ```bash
      npm install
      npm run serve
      ```
   3. The frontend will run on `http://localhost:8081`.

## Making Changes Locally
1. Create a new branch:
   1. In GitHub Desktop, click **Current Branch > New Branch**.
   2. Name it: `feature/<your-feature>` or `bugfix/<your-issue>`.
2. Make your changes and commit:
   1. Write a concise commit message.
3. Push your changes to GitHub:
   1. Click **Push origin**.
