package model.Containers;

import java.io.Serializable;
import java.util.*;

import model.Doors.Door;
import model.Game.Message;
import model.Items.*;

public class Inventory implements Serializable {

	private final LinkedHashMap<String, Item> ITEMS;

	public Inventory()
	{
		this.ITEMS = new LinkedHashMap<>();
	}

	public void addItem(Item item)
	{
		this.ITEMS.put(item.getTag(), item);
	}

	public Item getItem(String s)
	{
			return this.ITEMS.get(s);
	}

	public String getItemTag(int index)
	{
		List<String> itemTagsList = new ArrayList<String>(ITEMS.keySet());
		return itemTagsList.get(index);
	}

	public int getSize()
	{
		return this.ITEMS.size();
	}

	public void moveItem(String tag, Inventory inventory)
	{
		Item item = this.ITEMS.get(tag);

		if(item != null) {
			this.removeItem(tag);
			inventory.addItem(item);
		}

		else
		{
			Message.sendGameMessage("Error :> This item isn't in this inventory");
		}
	}

	public boolean isEmpty()
	{
		return this.ITEMS.isEmpty();
	}

	public void showItems()
	{
		for (Item i : this.ITEMS.values())
		{
			Message.sendGameMessage("\t- " + i.getTag() + " : " + i.getDescription());
		}
	}

	public void removeItem(String tag)
	{
		this.ITEMS.remove(tag);
	}
}