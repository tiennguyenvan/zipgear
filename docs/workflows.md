# Branching and Coding Guidelines

## Branching Workflow
1. **`main`**: Production-ready code.  
2. **`dev`**: For testing and development.  
3. **Feature branches**:  
   - Naming: `feature/<feature-name>`  
   - Example: `feature/user-management`  
4. **Bugfix branches**:  
   - Naming: `bugfix/<issue-name>`  
   - Example: `bugfix/login-error`

## Branching Rules
- Work on new features in a **feature branch**.
- For bug fixes, create a **bugfix branch**.
- Merge feature/bugfix branches into **`dev`** using Pull Requests (PRs).  
- **Tim Nguyen** reviews and approves all PRs before merging into `main`.

## Coding Guidelines
1. **Commit Messages**  
   - Format:  
     ```
     <type>: <short description>
     ```
   - Example:  
     ```
     feat: Add product creation API
     fix: Resolve checkout error
     ```
2. **Pull Requests (PRs)**  
   - PRs must have:
     - Clear title and description.
     - At least one reviewer (Tim Nguyen).
