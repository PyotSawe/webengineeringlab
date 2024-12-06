This represents the domain layer of this distributed system

## Applying policies
1. **Request Context**:
   - User: `{user-id: 2, roles: ["editor"]}`
   - Resource: `{resource-id: 200, owner-id: 2}`
   - Context: `{time: "2024-12-05T15:00:00Z", ip: "203.0.113.1"}`

2. **Policies**:
   - Ownership Policy: User owns the resource (`true`).
   - Admin Role Policy: User is not an admin (`false`).
   - Time Policy: Request within working hours (`true`).

3. **Evaluation**:
   - Policies are evaluated sequentially.
   - Access is granted as at least one policy evaluates to `true`.