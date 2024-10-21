# Using GitHub Desktop for Version Control and Team Collaboration

## 1. Cloning the Repository
1. Open **GitHub Desktop**.
2. Click **File > Clone Repository**.
3. Select **zipgear** from the repository list.
4. Choose a local path where the project will be stored.
5. Click **Clone**.

## 2. Creating a New Branch
1. In **GitHub Desktop**, click **Current Branch > New Branch**.
2. Enter a branch name:
   - **Feature:** `feature/<feature-name>`
   - **Bugfix:** `bugfix/<issue-name>`
3. Click **Create Branch**.

## 3. Making Changes and Committing
1. Open the project in your code editor.
2. Make your changes.
3. In GitHub Desktop, you’ll see your changes listed.
4. Write a concise commit message:
5. Click **Commit to <your-branch-name>**.

## 4. Pushing Changes to GitHub
1. In **GitHub Desktop**, click **Push origin** to upload your changes.

## 5. Creating a Pull Request (PR)
1. Go to [GitHub](https://github.com) and open the **zipgear** repository.
2. Click **Pull Requests > New Pull Request**.
3. Select **your branch** and compare it with the `dev` branch.
4. Add a title and description for the changes.
5. Click **Create Pull Request**.

## 6. Collaborating with the Team
- **Reviewing PRs:**  
- Review team members' PRs under **Pull Requests**.
- Leave comments if needed.
- **Merging PRs:**  
- Only **Tim Nguyen** merges PRs into `main`.
- PRs to `dev` can be merged if assigned.

## 7. Keeping Your Branch Up to Date
1. Switch to the `dev` branch in **GitHub Desktop**.
2. Click **Fetch origin** to pull the latest changes.
3. Switch back to your branch.
4. Click **Branch > Merge into Current Branch** to merge updates.
