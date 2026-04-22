# Fork workflow

This is a fork of [penpot/penpot](https://github.com/penpot/penpot). This
document is a cheat-sheet for keeping it in sync with upstream. It lives
only on this fork; do not include it in PRs proposed to the upstream repo.

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
