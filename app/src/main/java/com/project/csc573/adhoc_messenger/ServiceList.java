package com.project.csc573.adhoc_messenger;
/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.net.wifi.p2p.WifiP2pDevice;

import java.util.ArrayList;
import java.util.List;


/**
 * Class that represents a list of peer services.
 * This list contains all the device found during discovery phase of the wifi direct protocol.
 * This class use Singleton pattern.
 * <p></p>
 */
public class ServiceList {

    //ATTENTION DO NOT EXPOSE THIS ATTRIBUTE, BUT CREATE A SECURE METHOD TO MANAGE THIS LISTS!!!
    //SEE THE ANNOTATION, USE ONLY PRIVATE HERE WITHOUT GETTERS OR SETTERS!!!
    private final List<WiFiP2pService> serviceList;

    private static final ServiceList instance = new ServiceList();

    /**
     * Method to get the instance of this class.
     * @return instance of this class.
     */
    public static ServiceList getInstance() {
        return instance;
    }

    /**
     * Private constructor, because is a singleton class.
     */
    private ServiceList() {
        serviceList = new ArrayList<>();
    }


    /**
     * Method to add a service inside the list in a secure way.
     * The service is added only if isn't already inside the list.
     * @param service {@link WiFiP2pService} to add.
     */
    public Boolean addServiceIfNotPresent(WiFiP2pService service) {
        if(service==null) {
            return false;
        }

        boolean add = true;
        boolean update = false;
        int updateIndex = -1;



        for (WiFiP2pService element : serviceList) {
            if (element.deviceAddress.equals(service.deviceAddress)) {
//                if(element.isDirect && !service.isDirect){
                    // Skip if a direct peer is added via an indirect peer
                    add = false;
//                    update = false;
//                }
                /*if((element.isDirect && service.isDirect) || (!element.isDirect && !service.isDirect)) {
                    update = true;
                    updateIndex = serviceList.indexOf(element);
                    add = false; //already in the list
                }*/
            }
        }

//        if (update) {
//            serviceList.remove(updateIndex);
//            serviceList.add(service);
//        }

        if(add) {
            serviceList.add(service);
        }
        return add;
    }

    public Boolean updateExistingService(WiFiP2pService service) {
        if(service==null) {
            return false;
        }

        boolean update = false;
        int updateIndex = -1;
        for (WiFiP2pService element : serviceList) {
            if (element.deviceAddress.equals(service.deviceAddress)
                    && element.nextHopAddress.equals(service.nextHopAddress)
                    && element.instanceName.equals(service.instanceName)) {
                update = true;
                updateIndex = serviceList.indexOf(element);
            }

            if (element.deviceAddress.equals(service.deviceAddress)
                    && element.instanceName.equals(WiFiServiceDiscoveryActivity.SERVICE_INSTANCE_INDIRECT)
                    && service.instanceName.equals(WiFiServiceDiscoveryActivity.SERVICE_INSTANCE_DIRECT)) {
                update = true;
                updateIndex = serviceList.indexOf(element);
            }
        }

        if (update) {
            serviceList.remove(updateIndex);
            serviceList.add(service);
        }

        return update;
    }

    /**
     * Method to get a service from the list, using only the device.
     * This method use only the deviceAddress, not the device name, because sometimes Android doesn't
     * get the name, but only the mac address.
     * @param device WifiP2pDevice that you want to use to search the service.
     * @return The WiFiP2pService associated to the device or null, if the device isn't in the list.
     */
    public WiFiP2pService getServiceByDevice(WifiP2pDevice device) {
        if(device==null) {
            return null;
        }

        for (WiFiP2pService element : serviceList) {
            if (element.deviceAddress.equals(device.deviceAddress) ) {
                return element;
            }
        }
        return null;
    }

    public String getDeviceNames() {
        String deviceNames = "";
        for (WiFiP2pService element : serviceList) {
            if (!element.isDirect) continue;
            if (deviceNames.length() > 0)
                deviceNames = deviceNames + "###";
            deviceNames = deviceNames + element.deviceName + "%%" + element.deviceAddress + "%%"
                    + element.deviceStatus;
        }
        return deviceNames;
    }

    public List<String> showRoutingTable() {
        List<String> routes = new ArrayList<>();
        for (WiFiP2pService element : serviceList) {
            String route = element.deviceName + " - " + element.deviceAddress + " - "
                    + WiFiDirectServicesList.getDeviceStatus(element.deviceStatus) + " - "
                    + element.nextHopName + " - " + element.nextHopAddress;
            routes.add(route);
        }
        return routes;
    }

    public WiFiP2pService getNextHop(String destMAC) {
        for (WiFiP2pService element : serviceList) {
            if (element.deviceAddress.equals(destMAC)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Method to get the size of the list.
     * @return int that represent the size.
     */
    public int getSize() {
        return serviceList.size();
    }


    /**
     * Method to clear the list.
     */
    public void clear() {
        serviceList.clear();
    }

    /**
     * Method to get a element using only the real position in the list (from 0 to n).
     * @param position int that represents the position of the element.
     * @return A {@link WiFiP2pService } element from the list at position.
     * Attention, this method can throw a {@link IndexOutOfBoundsException },
     * if {@code position < 0 || position >= size()}
     */
    public WiFiP2pService getElementByPosition(int position) {
        return serviceList.get(position);
    }
}