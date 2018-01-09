/**
 *  CompositeChild
 *
 *  Josh Lyon - josh@boshdirect.com
 *  Do what you want with the code... it's to show the issue with composite devices and child.sendEvent()
 *
 */
metadata {
	definition (name: "CompositeChild", namespace: "joshualyon", author: "Josh Lyon") {
		capability "Switch"
		capability "Temperature Measurement"
	}

	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, decoration: "flat") {
            state "off", label: 'OFF', action: "on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
            state "on", label: 'ON', action: "off", icon: "st.switches.switch.on", backgroundColor: "#00a0dc"
        }
	}
}

// parse events into attributes
def parse(String description) {
	//Parsing would be handled in the parent

}

// handle commands
def on() {
	log.debug "Executing 'on' from child"
	parent.on(device.deviceNetworkId)
}

def off() {
	log.debug "Executing 'off' from child"
	parent.off(device.deviceNetworkId)
}