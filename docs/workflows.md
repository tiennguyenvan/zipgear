# Branching and Coding Guidelines

## Branching Workflow
1. **`main`**: Production-ready code.  
2. **`dev`**: For testing and development.  
3. **Sprint branches**:  
   - Naming: `sprint-<sprint-name>`  
   - Example: `sprint-01`  
4. All members work on single sprint for easy testing. Only **Tim Nguyen** can marge branches into **`dev`** using Pull Requests (PRs), also reviews and approves all PRs before merging into `main`.

## Coding Guidelines
1. **Commit Messages**
   - Provide a short description:  
     ```
     <type>: <short description>
     ```
   - Example:  
     ```
     New: Add product creation API
     Fix: Resolve checkout error
     ```
2. **Pull Requests (PRs)**  
   - PRs must have:
     - Clear title and description.
     - At least one reviewer (Tim Nguyen).
