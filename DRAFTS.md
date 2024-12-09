## Saturday, December 07 2024

1). Install all necessary dependecies == Done
2). config not a source file. Mv it to conf @= level to src/
3). Download browser architecture(drawin-representation) and expand to fit architecture found in chrome, brave or firefox == done

4). Study http-kit and use it to build user-agents apps that can connect to any server endpoints and know what otherclients exists both clojure and java that we can use
4b). We can use it to built tunnels, vpns, downloaders
4c). Can we use it to built part of our browser
``` plaintext
src/
└── jaminlab
    ├── config
    │   ├── network.clj            ;; Network configurations
    │   ├── settings.clj           ;; Global settings (browser preferences, etc.)
    │   └── ui.clj                 ;; UI-related configurations
    ├── networking
    │   ├── http.clj               ;; HTTP/HTTPS protocol handling
    │   ├── websocket.clj          ;; WebSocket protocol handling
    │   └── proxy.clj              ;; Proxy handling
    ├── ui
    │   ├── rendering
    │   │   ├── dom.clj            ;; DOM rendering logic
    │   │   ├── layout.clj         ;; Layout algorithm
    │   │   └── css.clj            ;; CSS styling engine
    │   ├── graphics
    │   │   ├── canvas.clj         ;; 2D canvas implementation
    │   │   ├── webgl.clj          ;; WebGL for 3D rendering
    │   │   └── shaders.clj        ;; Shader handling
    │   ├── events
    │   │   ├── mouse.clj          ;; Mouse events (click, hover, etc.)
    │   │   ├── keyboard.clj       ;; Keyboard events (key press, etc.)
    │   │   └── touch.clj          ;; Touch events (for mobile support)
    │   ├── layout
    │   │   ├── ui-components.clj  ;; Components like buttons, inputs, etc.
    │   │   └── navigation.clj     ;; Navigation bar, tabs, etc.
    │   └── browser.clj            ;; Browser window and tab management
    ├── js-engine
    │   ├── interpreter.clj        ;; JavaScript interpreter
    │   ├── runtime.clj            ;; JavaScript runtime environment (VM)
    │   ├── parser.clj             ;; JavaScript parsing (Lexical analysis, parsing)
    │   ├── evaluator.clj          ;; Expression evaluation and execution
    │   └── async.clj              ;; Async tasks (Promises, setTimeout, etc.)
    ├── rendering
    │   ├── html-parser.clj        ;; HTML parsing
    │   ├── css-parser.clj         ;; CSS parsing
    │   ├── render-tree.clj        ;; Render tree generation from DOM
    │   ├── paint.clj              ;; Paint phase (draw pixels)
    │   └── composite.clj          ;; Compositing layers (rendering on screen)
    ├── network
    │   ├── dns.clj                ;; DNS lookup and caching
    │   ├── tcp.clj                ;; TCP connection management
    │   ├── tls.clj                ;; TLS/SSL for secure connections
    │   ├── http.clj               ;; HTTP/HTTPS protocol handling
    │   └── caching.clj            ;; HTTP caching (LocalStorage, Cache API)
    ├── security
    │   ├── ssl.clj                ;; SSL/TLS handling (encryption)
    │   ├── csrf.clj               ;; CSRF protection
    │   ├── csp.clj                ;; Content Security Policy (CSP)
    │   ├── cookie.clj             ;; Cookie management and security
    │   ├── auth.clj               ;; Authentication mechanisms (OAuth, JWT, etc.)
    │   └── sandbox.clj            ;; Security sandbox for running JavaScript
    ├── persistence
    │   ├── local-storage.clj      ;; Local storage handling (IndexedDB, LocalStorage)
    │   ├── session-storage.clj    ;; Session storage handling
    │   └── cookies.clj            ;; Cookie handling
    ├── devtools
    │   ├── debugger.clj           ;; Debugging interface (inspect elements, step-through code)
    │   ├── profiler.clj           ;; Profiling tools for performance analysis
    │   └── console.clj            ;; Console output (for logs and errors)
    ├── web
    │   ├── html.clj               ;; HTML parsing and rendering
    │   ├── css.clj                ;; CSS handling (styling)
    │   ├── javascript.clj         ;; JavaScript execution context and interaction
    │   ├── dom.clj                ;; DOM tree handling
    │   ├── xhr.clj                ;; XMLHttpRequest and Fetch API
    │   └── service-workers.clj    ;; Service Workers and Progressive Web Apps (PWA)
    ├── integration
    │   ├── extension-api.clj      ;; Extension API (for browser extensions)
    │   └── web-platform-api.clj   ;; APIs exposed by the browser to web pages (geolocation, etc.)
    ├── tests
    │   ├── unit.clj               ;; Unit tests for individual components
    │   ├── integration.clj        ;; Integration tests
    │   └── ui-tests.clj           ;; UI tests (e.g., for front-end interaction)
    └── main.clj                    ;; Main entry point to the browser
```