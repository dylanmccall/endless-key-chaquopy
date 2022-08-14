from java import jclass

from kolibri.dist.magicbus.plugins import SimplePlugin

Intent = jclass("android.content.Intent")

class KolibriAndroidPlugin(SimplePlugin):
    def __init__(self, bus, android_context):
        self.bus = bus
        self.android_context = android_context
        self.bus.subscribe("SERVING", self.SERVING)

    def SERVING(self, port):
        from kolibri.plugins.app.utils import interface
        from kolibri.utils.server import get_urls
        from kolibri.core.device.models import DeviceAppKey

        _, base_urls = get_urls(listen_port=port)
        base_url = base_urls[0]
        app_key = DeviceAppKey.get_app_key()

        start_intent = Intent()
        start_intent.setPackage("org.endlessos.endlesskey")
        start_intent.setAction("org.endlessos.endlesskey.SERVING")
        start_intent.putExtra("baseUrl", base_url or "")
        # start_intent.putExtra("initializeUrl", initialize_url or "")
        self.android_context.sendBroadcast(start_intent)
