#!/bin/bash

jar_file=$1

[[ -f "$jar_file" ]] || exit 1
[[ -f "package.json" ]] || exit 1
[[ -f "esp32partitiontool.py" ]] || exit 1

version=`jq -r .version package.json`
new_version=$version
commit=`git rev-parse --short HEAD`

echo "Current version from package.json: $version"

if [ "$GITHUB_EVENT_NAME" == "release" ]; then
  event_json=`cat $GITHUB_EVENT_PATH`
  action=`echo $event_json | jq -r '.action'`
  if [ "$action" == "published" ]; then
    # version forced by tag
    release_tag=`echo $event_json | jq -r '.release.tag_name'`
    new_version=${release_tag#v}
  else
    new_version="$version-$commit"
  fi
else
  new_version="$version-$commit"
fi

if [ "$version" != "$new_version" ]; then
  echo "$(jq --arg newval "$new_version" '.version |= $newval' package.json)" > package.json
  jq . package.json
  echo "Updated Version = $new_version"
fi


# platformio requirement: append date to folder name
tmp_dir="tmp/esp32partitiontool-$(date +"%F")"
archive_file="esp32partitiontool-platformio.zip"
# cleanup from previous build
rm -Rf "$tmp_dir"
mkdir -p "$tmp_dir"
cp "$jar_file" "$tmp_dir/ESP32PartitionTool.jar" || exit 1
# TODO: raise package.version in json
cp "package.json" $tmp_dir/
cp "esp32partitiontool.py" $tmp_dir/
cd tmp
rm -f ../$archive_file
zip -rq ../$archive_file . || exit 1
cd ..
rm -Rf tmp

[[ -f "$archive_file" ]] || exit 1

echo "Platformio Package: $archive_file"
