import json, argparse, os

def get_conf_from_file(fn):
    config = {}
    if os.path.exists(fn):
        with open(fn) as cf:
            config = json.load(cf)
    return config

def get_conf():
    # parse arguments
    parser = argparse.ArgumentParser()
    parser.add_argument("-c", "--conf", help="config file", default="/etc/mqtt-relay.conf")
    args = parser.parse_args()
    return get_conf_from_file(args.conf)

