**CQRS** is a pattern that separates the handling of commands (actions that change state) from queries (actions that return state). This helps scale the system by separating the read model from the write model and handling them independently. We can apply CQRS to our order system by having separate services for handling commands (e.g., create, update) and queries (e.g., get order details).


---

**commands** (which modify the system state) and **queries** (which fetch data without changing the state)