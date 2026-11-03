#!/bin/bash
# Takes a masked screenshot of simulator $1 for every checkpoint the UI test leaves in $2, into $3 with prefix $4.
U=$1; D=$2; OUT=$3; P=$4
mkdir -p "$D" "$OUT"
while [ ! -e "$D/stop" ]; do
    for f in "$D"/*.ready; do
        [ -e "$f" ] || continue
        n=$(basename "$f" .ready)
        xcrun simctl io "$U" screenshot --mask=black "$OUT/$P-$n.png" >/dev/null 2>&1
        mv "$f" "$D/$n.done"
        echo "captured $n"
    done
    sleep 0.2
done
