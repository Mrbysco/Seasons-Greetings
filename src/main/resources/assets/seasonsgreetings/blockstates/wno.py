import json
import os

#################################################################

def ensure_outputs_exist():
    for file in os.walk(_cwd + f"\\{input_path}\\"):
        try:
            os.mkdir(file[0].replace(input_path, output_path))
        except:
            continue

# Function to replace values in a dictionary
def dict_replace_value(d, old, new):
    x = {}
    for k, v in d.items():
        if isinstance(v, dict):
            v = dict_replace_value(v, old, new)
        elif isinstance(v, list):
            v = list_replace_value(v, old, new)
        elif isinstance(v, str):
            # Replace only the exact "seasonsgreetings:block/gift_box/main/{color}" pattern
            target_old = f"seasonsgreetings:block/gift_box/main/{old}"
            target_new = f"seasonsgreetings:block/gift_box/main/{new}"
            if old and new and target_old in v:
                v = v.replace(target_old, target_new)
            v = v.replace("{namespace}", f"{namespace}")
        x[k] = v
    return x

# Function to replace values in a list
def list_replace_value(l, old, new):
    x = []
    for e in l:
        if isinstance(e, list):
            e = list_replace_value(e, old, new)
        elif isinstance(e, dict):
            e = dict_replace_value(e, old, new)
        elif isinstance(e, str):
            # Replace only the exact "seasonsgreetings:block/gift_box/main/{color}" pattern
            target_old = f"seasonsgreetings:block/gift_box/main/{old}"
            target_new = f"seasonsgreetings:block/gift_box/main/{new}"
            if old and new and target_old in e:
                e = e.replace(target_old, target_new)
            e = e.replace("{namespace}", f"{namespace}")
        x.append(e)
    return x

#################################################################

colors = [
    "orange",
    "magenta",
    "light_blue",
    "yellow",
    "lime",
    "pink",
    "gray",
    "light_gray",
    "cyan",
    "purple",
    "blue",
    "brown",
    "green",
    "red",
    "black"
]

woods = [
    "spruce",
    "birch",
    "jungle",
    "acacia",
    "dark_oak",
    "mangrove",
    "crimson",
    "warped",
    "bamboo",
    "cherry"
]

namespace = "another_furniture"

cwd = os.getcwd()

process_specific = input("process specific file (or enter to skip): ")
if process_specific == "oak":
    typea = "oak"
    files_to_process = [f for f in os.listdir(cwd) if os.path.isfile(os.path.join(cwd, f)) and f.startswith("oak")]
elif process_specific != "":
    files_to_process = [process_specific + ".json"]
else:
    typea = "white"
    files_to_process = [f for f in os.listdir(cwd) if os.path.isfile(os.path.join(cwd, f)) and f.startswith("white")]

for file in files_to_process:
    should_process = input("should process file " + file + "? ")
    if should_process == "" or should_process.lower() == "y" or should_process.lower() == "yes":
        f = open(file, "r")
        data = json.loads(f.read())
        if "white" in file:
            for color in colors:
                new_data = dict_replace_value(data, "white", color)
                with open(file.replace("white", color), "w+") as f:
                    f.write(json.dumps(new_data, indent=4) + "\n")
        elif "oak" in file:
            for wood in woods:
                new_data = dict_replace_value(data, "oak", wood)
                with open(file.replace("oak", wood), "w+") as f:
                    f.write(json.dumps(new_data, indent=4) + "\n")