def update_kolibri_environ():
    import os
    import re

    script_dir = os.path.dirname(os.path.abspath(__file__))

    # TODO: Get signature key issuing organization
    signing_org = "Learning Equality"

    if signing_org == "Learning Equality":
        runmode = "android-testing"
    elif signing_org == "Android":
        runmode = "android-debug"
    elif signing_org == "Google Inc.":
        runmode = ""  # Play Store!
    else:
        runmode = "android-" + re.sub(r"[^a-z ]", "", signing_org.lower()).replace(" ", "-")

    os.environ["KOLIBRI_RUN_MODE"] = runmode
    os.environ["KOLIBRI_PROJECT"] = "endless-key-android"

    # Used by Kolibri to detect whether it is running on Android
    # <https://github.com/learningequality/kolibri/blob/develop/kolibri/utils/android.py>
    os.environ["ANDROID_ARGUMENT"] = "yes"

    # os.environ["TZ"] = get_timezone_name()
    # os.environ["LC_ALL"] = "en_US.UTF-8"

    # os.environ["HOME"] is set to the app's internal storage directory
    os.environ["KOLIBRI_HOME"] = os.path.join(os.environ["HOME"], "kolibri")

    # endless_key_uris = get_endless_key_uris()
    # if endless_key_uris is not None:
    #     content_uri = DynamicWhiteNoise.encode_root(endless_key_uris["content"])
    #     logging.info("Setting KOLIBRI_CONTENT_FALLBACK_DIRS to %s", content_uri)
    #     os.environ["KOLIBRI_CONTENT_FALLBACK_DIRS"] = content_uri

    # os.environ["KOLIBRI_APK_VERSION_NAME"] = get_version_name()
    # os.environ["DJANGO_SETTINGS_MODULE"] = "kolibri_app_settings"

    AUTOPROVISION_FILE = os.path.join(script_dir, "automatic_provision.json")
    if os.path.exists(AUTOPROVISION_FILE):
        os.environ["KOLIBRI_AUTOMATIC_PROVISION_FILE"] = AUTOPROVISION_FILE

    os.environ["KOLIBRI_CHERRYPY_THREAD_POOL"] = "2"

    # os.environ["KOLIBRI_APPS_BUNDLE_PATH"] = os.path.join(script_dir, "apps-bundle", "apps")
    # os.environ["KOLIBRI_CONTENT_COLLECTIONS_PATH"] = os.path.join(script_dir, "collections")

    # Secure = autoclass("android.provider.Settings$Secure")

    # node_id = Secure.getString(get_activity().getContentResolver(), Secure.ANDROID_ID)

    # # Don't set this if the retrieved id is falsy, too short, or a specific
    # # id that is known to be hardcoded in many devices.
    # if node_id and len(node_id) >= 16 and node_id != "9774d56d682e549c":
    #     os.environ["MORANGO_NODE_ID"] = node_id


def init_kolibri():
    update_kolibri_environ()

    from kolibri.utils.main import initialize

    initialize(skip_update=True)


def main(android_context):
    init_kolibri()

    import logging
    import os

    from kolibri.utils.conf import KOLIBRI_HOME
    from kolibri.utils.server import KolibriProcessBus
    from kolibri_android_plugin import KolibriAndroidPlugin

    kolibri_bus = KolibriProcessBus(
        port=0,
        zip_port=0,
        background=False,
    )

    kolibri_android_plugin = KolibriAndroidPlugin(kolibri_bus, android_context)
    kolibri_android_plugin.subscribe()

    logging.info("Entering Kolibri server service")
    logging.info("Home folder: {}".format(KOLIBRI_HOME))
    logging.info("Timezone: {}".format(os.environ.get("TZ", "(default)")))

    # start the kolibri server
    kolibri_bus.run()
