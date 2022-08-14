# Wrapper to make Chaquopy's java module look like pyjnius
# This is necessary for compatibility with the following Kolibri code:
# - https://github.com/learningequality/kolibri/blob/v0.15.6/kolibri/utils/system.py#L150-L169
# - https://github.com/learningequality/kolibri/blob/v0.15.6/kolibri/core/discovery/utils/filesystem/posix.py#L159-L168
# TODO: Instead, patch Kolibri

from java import jclass

def autoclass(*args, **kwargs):
    return jclass(*args, **kwargs)
