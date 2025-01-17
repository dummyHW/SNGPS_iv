package LaPrac;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class Subject implements IObservable, Runnable{
	private ArrayList<IObserver> observers;
	private MessageListener listener;
	private String mensaje = new String();

	public Subject() {
		this.observers = new ArrayList<IObserver>();
	}
	
	public ArrayList<IObserver> getObservers() {
		return observers;
	}
	public void setObservers(ArrayList<IObserver> observers) {
		this.observers = observers;
	}
	
	@Override
	public void addObserver(IObserver observador) {
		this.observers.add(observador);
	}
	@Override
	public void removeObserver(IObserver observador) {
		this.observers.remove(observador);
	}
	@Override
	public void notifyObservers() {
		for (Iterator<IObserver> it = observers.iterator(); it.hasNext();) {
			IObserver iObserver = it.next();
			iObserver.actualizar();
		}
	}
	
	public MessageListener getListener() {
		return listener;
	}

	public void setListener(MessageListener listener) {
		this.listener = listener;
	}
	
	
	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public void start() {
		Thread hilo = new Thread(this);
		hilo.start();
	}
	
	public void leerPuertos() {
		SerialPort[] comPorts = SerialPort.getCommPorts();
		for (SerialPort s: comPorts) {
			System.out.println("Puerto disponible: " + s);
		}
		
		// Suponemos que solo hay uno y es el primero
		SerialPort comPort = SerialPort.getCommPorts()[0];
		System.out.println("Puerto usado: " + comPort);
		comPort.openPort();
		comPort.setParity(0);
		comPort.setBaudRate(4800);
		comPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
		System.out.println("Puerto abierto: "+ comPort.isOpen());
		
		Subject cosilla = this;
		
        comPort.addDataListener(
				
				new MessageListener() {

		   @Override
		   public void serialEvent(SerialPortEvent event)
		   {
		      byte[] delimitedMessage = event.getReceivedData();
		      
		      StringBuilder mensaje = new StringBuilder();
		      
		      for (int i = 0; i < delimitedMessage.length; ++i) {
			         //System.out.print((char)delimitedMessage[i]);
			         mensaje.append(Character.toString((char)delimitedMessage[i]));
		      }
		      
		      cosilla.setMensaje(mensaje.toString());
		      cosilla.notifyObservers();
		   }
		});
		
	}
	
	public void run() {
		synchronized (this) {
			leerPuertos();
		}
	}

	@Override
	public String toString() {
		return "Subject [observers=" + observers + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(observers);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Subject other = (Subject) obj;
		return Objects.equals(observers, other.observers);
	}

}
