# Convenience targets for this fork. See FORK.md for the full workflow.

DEVENV_CONTAINER := penpot-devenv-main
TMUX_SESSION     := penpot

.DEFAULT_GOAL := help

.PHONY: help dev stop tmux sync-upstream

help: ## Show this help.
	@awk 'BEGIN {FS = ":.*##"; print "Targets:"} /^[a-zA-Z_-]+:.*##/ { printf "  %-16s %s\n", $$1, $$2 }' $(MAKEFILE_LIST)

dev: ## Start the dev environment (containers + tmux). Attaches your terminal.
	./manage.sh run-devenv

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
