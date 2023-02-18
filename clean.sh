#!/bin/sh

# Find all build directories and print their size
find . -name 'build' -type d -prune -print | xargs du -chs

# Remove all build directories
find . -name 'build' -type d -prune -print -exec rm -rf '{}' \;

# Remove all .gradle directories
find . -name '.gradle' -type d -prune -print -exec rm -rf '{}' \;
