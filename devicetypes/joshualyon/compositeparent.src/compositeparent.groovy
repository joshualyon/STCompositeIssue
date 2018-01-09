/**
 *  CompositeParent
 *
 *  Josh Lyon - josh@boshdirect.com
 *  Do what you want with the code... it's to show the issue with composite devices and child.sendEvent()
 *
 */
metadata {
	definition (name: "CompositeParent", namespace: "joshualyon", author: "Josh Lyon") {
		capability "Switch"
		capability "Temperature Measurement"
        
        command "allOn"
    	command "allOff"
        command "parentOn"
        command "parentOff"
	}

	tiles(scale: 2) {
    	standardTile("switch", "device.switch", width: 2, height: 2, decoration: "flat") {
            state "off", label: 'ALL', action: "allOn", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
            state "on", label: 'ALL', action: "allOff", icon: "st.switches.switch.on", backgroundColor: "#00a0dc"
        }
        standardTile("parentOn", "device.switch", decoration: "flat", height: 1, width:1) {
            state "default", action:"parentOn", label: "On"
        }
        standardTile("parentOff", "device.switch", decoration: "flat", height: 1, width:1) {
            state "default", action:"parentOff", label: "Off"
        }
        
		childDeviceTile("switch1", "switch1", height: 2, width: 2, childTileName: "switch")
        childDeviceTile("switch2", "switch2", height: 2, width: 2, childTileName: "switch")
        
        main("switch")
        details(["switch", "switch1", "switch2", "parentOn", "parentOff"])
	}
}

def installed(){
	createChildDevices()
}

def createChildDevices(){
	for(i in 1..2){
    	addChildDevice(
        	"CompositeChild", 
        	"${device.deviceNetworkId}.switch${i}", 
            null, 
            [
               completedSetup: true, 
               label: "${device.displayName} (Switch ${i})", 
               isComponent: true, 
               componentName: "switch$i", 
               componentLabel: "Switch $i"
            ]
        )   
    }
}

//MANUAL COMMANDS JUST FOR THE PARENT (normally not needed, but just for demo)
def parentOn(){ 
	log.debug "Parent MANUAL ON"
	sendEvent(name: "switch", value: "on")
}
def parentOff(){ 
	log.debug "Parent MANUAL OFF"
	sendEvent(name: "switch", value: "off")
}


// COMMANDS INTENDED TO CHANGE ALL CHILDREN
def allOn(){
	allSwitch("on")
}
def allOff(){
	allSwitch("off")
}
def allSwitch(value){
	log.debug "Turning all children $value: $childDevices"
    
	childDevices.each{ child -> 
    	log.debug "--Adjusting child: $child"
        child.sendEvent(name: "switch", value: value)
        //!!! it doesn't matter if child.on or child.sendEvent or child.emitEvent (custom method in child) are used... the loop stops when the child event is issued
    }
}

// COMMANDS INTENDED TO CHANGE INDIVIDUAL CHILDREN
def on(childDNI) {
	changeSwitch(childDNI, "on")
}

def off(childDNI) {
	changeSwitch(childDNI, "off")
}
def changeSwitch(childDNI, value){
	log.debug "Executing '$value' from parent for $childDNI"
    def child1 = childDevices.find{it.deviceNetworkId.endsWith("1")}
	def child2 = childDevices.find{it.deviceNetworkId.endsWith("2")}
    
    if(childDNI.endsWith("1")){ child1.sendEvent(name: "switch", value: value) }
    else if(childDNI.endsWith("2")){ child2.sendEvent(name: "switch", value: value) }
	
    //!!! We will never get to this part of the code... it appears that when the child event is created, further commands are not processed
    
    //if either child is on, turn the parent on
    if(child1.currentValue("switch") == "on" || child2.currentValue("switch") == "on"){
    	sendEvent(name: "switch", value: "on")
    }
    //if both children are off, turn the parent off
    if(child1.currentValue("switch") == "off" && child2.currentValue("switch") == "off"){
        sendEvent(name: "switch", value: "off")
    }
}