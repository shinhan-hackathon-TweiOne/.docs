package com.example.shinhantime.networks

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.shinhantime.networks.UwbControllerCommunicator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

val CTF_SERVICE_UUID: UUID = UUID.fromString("8c380000-10bd-4fdb-ba21-1922d6cf860d")
val PASSWORD_CHARACTERISTIC_UUID: UUID = UUID.fromString("8c380001-10bd-4fdb-ba21-1922d6cf860d")
val NAME_CHARACTERISTIC_UUID: UUID = UUID.fromString("8c380002-10bd-4fdb-ba21-1922d6cf860d")

@Suppress("DEPRECATION")
class BLEDeviceConnection @RequiresPermission("PERMISSION_BLUETOOTH_CONNECT") constructor(
    private val context: Context,
    private val bluetoothDevice: DeviceInfo
) {
    val isConnected = MutableStateFlow(false)
    val controleeRead = MutableStateFlow<String?>(null)
    val successfulNameWrites = MutableStateFlow(0)
    val services = MutableStateFlow<List<BluetoothGattService>>(emptyList())

    private val callback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val connected = newState == BluetoothGatt.STATE_CONNECTED
            if (connected) {
                Log.d("BLE", "Connected to device: ${bluetoothDevice.device.name}")
                discoverServicesWithDelay()  // 서비스 탐색 시작 (지연 포함)
            } else {
                Log.e("BLE", "Failed to connect or disconnected from device: ${bluetoothDevice.device.name}")
            }
            isConnected.value = connected
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                services.value = gatt.services
                Log.d("BLE", "Services discovered: ${gatt.services.size} services found.")
                for (service in gatt.services) {
                    Log.d("BLE", "Service UUID: ${service.uuid}")
                }
            } else {
                Log.e("BLE", "Service discovery failed with status: $status")
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (characteristic.uuid == PASSWORD_CHARACTERISTIC_UUID && status == BluetoothGatt.GATT_SUCCESS) {
                controleeRead.value = String(characteristic.value)
                Log.d("BLE", "Characteristic read successfully: ${characteristic.uuid}")
            } else {
                Log.e("BLE", "Characteristic read failed for UUID: ${characteristic.uuid}, status: $status")
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (characteristic.uuid == NAME_CHARACTERISTIC_UUID && status == BluetoothGatt.GATT_SUCCESS) {
                successfulNameWrites.update { it + 1 }
                Log.d("BLE", "Characteristic write successful: ${characteristic.uuid}")
            } else {
                Log.e("BLE", "Characteristic write failed for UUID: ${characteristic.uuid}, status: $status")
            }
        }
    }

    private var gatt: BluetoothGatt? = null
    private val uwbCommunicator = UwbControllerCommunicator(context)

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun connect() {
        gatt = bluetoothDevice.device.connectGatt(context, false, callback)
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun discoverServicesWithDelay() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(500)  // 500ms 지연 (필요에 따라 조정 가능)
            val success = gatt?.discoverServices()
            Log.v("bluetooth", "Discover services status: $success")
        }
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun readPassword() {
        val service = gatt?.getService(CTF_SERVICE_UUID)
        if (service == null) {
            Log.e("BLE", "CTF_SERVICE_UUID not found on the device.")
            return
        }

        val characteristic = service.getCharacteristic(PASSWORD_CHARACTERISTIC_UUID)
        if (characteristic == null) {
            Log.e("BLE", "PASSWORD_CHARACTERISTIC_UUID not found in the service.")
            return
        }

        val success = gatt?.readCharacteristic(characteristic)
        Log.v("bluetooth", "Read status: $success")
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun writeName() {
        val service = gatt?.getService(CTF_SERVICE_UUID)
        if (service == null) {
            Log.e("BLE", "CTF_SERVICE_UUID not found on the device.")
            return
        }

        val characteristic = service.getCharacteristic(NAME_CHARACTERISTIC_UUID)
        if (characteristic == null) {
            Log.e("BLE", "NAME_CHARACTERISTIC_UUID not found in the service.")
            return
        }

        // UWB Controller의 주소와 채널 정보를 가져옵니다.
        val uwbAddress = uwbCommunicator.getUwbAddress()
        val uwbChannel = uwbCommunicator.getUwbChannel()

        // 주소와 채널 정보를 합쳐 characteristic.value에 설정합니다.
        val dataToSend = "$uwbAddress/$uwbChannel"
        characteristic.value = dataToSend.toByteArray()

        val success = gatt?.writeCharacteristic(characteristic)
        Log.v("bluetooth", "Write status: $success")

        val latestControleeRead = controleeRead.value
        Log.d("uwb", "controlee: $latestControleeRead")
        if (latestControleeRead != null) {
            // 4. BLEClient - BLEDeviceConnection으로 전송 완료가 될 시
            // 4.1 UwbControllerCommunicator - UWB Session 생성
            uwbCommunicator.startCommunication(latestControleeRead)
        } else {
            Log.d("uwb", "controleeRead is null, cannot start communication")
        }
    }
}
