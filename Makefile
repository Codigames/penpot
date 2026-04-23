# Convenience targets for this fork. See FORK.md for the full workflow.

DEVENV_CONTAINER := penpot-devenv-main
TMUX_SESSION     := penpot
APP_URL          := https://localhost:3449/
MAILCATCHER_URL  := http://localhost:1080/

.DEFAULT_GOAL := help

.PHONY: help dev stop tmux sync-upstream

help: ## Show this help.
	@awk 'BEGIN {FS = ":.*##"; print "Targets:"} /^[a-zA-Z_-]+:.*##/ { printf "  %-16s %s\n", $$1, $$2 }' $(MAKEFILE_LIST)

dev: ## Start containers + backend + frontend watch (no exporter, no Chrome). Prints URL when ready.
	@./manage.sh start-devenv
	@docker exec $(DEVENV_CONTAINER) bash -lc '\
	  if tmux has-session -t $(TMUX_SESSION) 2>/dev/null; then \
	    echo "[make dev] tmux session $(TMUX_SESSION) already running, leaving it as-is."; \
	  else \
	    echo "[make dev] starting frontend watch + backend in tmux session $(TMUX_SESSION)..."; \
	    tmux new-session -d -s $(TMUX_SESSION) -n frontend; \
	    tmux send-keys -t $(TMUX_SESSION):frontend "cd /home/penpot/penpot/frontend && ./scripts/watch app" Enter; \
	    tmux new-window  -t $(TMUX_SESSION)    -n backend; \
	    tmux send-keys -t $(TMUX_SESSION):backend  "cd /home/penpot/penpot/backend && ./scripts/start-dev" Enter; \
	  fi'
	@echo "[make dev] waiting for backend to report ready (cold start can take a few minutes)..."
	@for i in $$(seq 1 90); do \
	  if docker exec $(DEVENV_CONTAINER) curl -sf http://localhost:6060/readyz >/dev/null 2>&1; then \
	    echo "[make dev] backend ready."; \
	    echo ""; \
	    echo "  App:        $(APP_URL)"; \
	    echo "  Mailcatcher: $(MAILCATCHER_URL)"; \
	    echo "  Logs:       make tmux"; \
	    exit 0; \
	  fi; \
	  sleep 5; \
	done; \
	echo "[make dev] backend did not report ready in time. Check logs with: make tmux"; \
	exit 1

stop: ## Stop the dev environment containers (volumes preserved).
	./manage.sh stop-devenv

tmux: ## Re-attach to the running devenv tmux session.
	docker exec -ti $(DEVENV_CONTAINER) tmux -2 attach-session -t $(TMUX_SESSION)

sync-upstream: ## Fetch upstream and fast-forward develop, then push to origin.
	@test "$$(git rev-parse --abbrev-ref HEAD)" = develop \
		|| { echo "Switch to 'develop' first (this rule won't switch for you)."; exit 1; }
	@test -z "$$(git status --porcelain)" \
		|| { echo "Working tree not clean. Commit or stash first."; exit 1; }
	git fetch upstream
	git merge --ff-only upstream/develop
	git push origin develop
