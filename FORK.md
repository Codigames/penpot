# Fork workflow

This is a fork of [penpot/penpot](https://github.com/penpot/penpot). This
document is a cheat-sheet for keeping it in sync with upstream. It lives
only on this fork; do not include it in PRs proposed to the upstream repo.

A `Makefile` at the repo root wraps the most common operations (`make
help` lists them). Use `make dev` / `make stop` / `make sync-upstream` if
you prefer not to type the underlying commands. The detailed rules below
are still the source of truth — the Makefile is just shortcuts.

## Remotes

```
origin    git@github.com:Codigames/penpot.git    # this fork (push + fetch)
upstream  https://github.com/penpot/penpot.git   # penpot official (fetch only)
```

Verify with `git remote -v`. Never push to `upstream`.

## Branch tracking

- `develop` tracks `origin/develop` (your fork). Day-to-day work and sync go
  through `origin`.
- Feature branches (`feat/*`, `fix/*`, ...) are created from an up-to-date
  `develop` and pushed to `origin`.

## Syncing `develop` with upstream

When the original project adds commits you want to pull in:

```bash
git checkout develop
git fetch upstream
git merge --ff-only upstream/develop       # refuses if local has diverged
git push origin develop                    # mirror into your fork
```

If `--ff-only` fails it means you have local commits on `develop`. Don't.
Feature work belongs on feature branches; `develop` should stay a clean
mirror.

## Rebasing a feature branch on the refreshed `develop`

After syncing `develop`, rebase any open feature branch so it sits on top
of the latest upstream code:

```bash
git checkout feat/your-branch
git rebase develop
# resolve conflicts if any, then:
#   git add <files> && git rebase --continue
# or abort with:
#   git rebase --abort
git push --force-with-lease origin feat/your-branch
```

`--force-with-lease` is the safe variant of `--force`: it refuses the push
if someone else added commits to the remote branch since your last fetch.

## Starting a new feature branch

```bash
git checkout develop
git fetch upstream
git merge --ff-only upstream/develop
git push origin develop
git checkout -b feat/your-feature
# ...work...
git push -u origin feat/your-feature
```

## Release branch (deploying multiple features together)

When you have more than one feature branch and want to deploy them
together, keep a dedicated `release` branch built from `develop` + every
feature. **Never commit directly to `release`** — only rebuild it.

Add or update a feature in the release:

```bash
git checkout release
git merge --no-ff feat/image-9-slice
git merge --no-ff feat/other-feature
git push origin release
```

`--no-ff` forces a merge commit per feature so each one stays
identifiable in the history, and can be reverted wholesale with
`git revert -m 1 <merge-sha>`.

Rebuilding `release` after syncing `develop` from upstream (recommended
because the old merges become stale):

```bash
# 1. Sync develop with upstream first (see section above).

# 2. Rebase each feature branch on the refreshed develop.
for b in feat/image-9-slice feat/other-feature; do
  git checkout $b
  git rebase develop
  git push --force-with-lease origin $b
done

# 3. Recreate release from scratch (cleaner than merging over and over).
git checkout develop
git branch -D release 2>/dev/null || true
git checkout -b release
git merge --no-ff feat/image-9-slice
git merge --no-ff feat/other-feature
git push --force-with-lease origin release
```

Rebuilds require force-push because `release` is a reconstructed branch,
not a linear advance. `--force-with-lease` refuses the push if the remote
moved behind your back.

Conflict resolution lives on the feature branch (during the rebase),
never on `release`. This keeps each fix in the feature it belongs to.

Deploy / build artifacts from `release`.

## Proposing changes upstream

If you want to contribute a branch back to penpot/penpot:

1. Read `CONTRIBUTING.md` in the repo root.
2. Every commit must be signed off (`git commit -s`) with your real name;
   the DCO is mandatory.
3. Open a GitHub issue first to discuss the feature (upstream requires this
   for non-trivial changes).
4. From your fork's branch page on GitHub click "Contribute" → "Open pull
   request" with base `penpot/penpot:develop`.

## Fetching upstream tags and release branches

```bash
git fetch upstream --tags
git fetch upstream staging          # release stabilization branch
```
