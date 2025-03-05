package steve;

import java.io.FileNotFoundException;

import net.openrs.cache.Cache;
import net.openrs.cache.FileStore;
import net.openrs.cache.def.RSInterface;

public class Test {

	public static void main(String[] args) throws FileNotFoundException {
		Cache cache = new Cache(FileStore.open("C:\\Users\\stephen\\.xaeron_store_32\\runescape"));
		RSInterface rsInterface = RSInterface.get(cache, 387);
		System.out.println(rsInterface);
		for (RSInterface inter : rsInterface.children) {
			System.out.println("lala");
			if (inter.contentType == 5) { //sprite
				System.out.println(inter.spriteId);
			}
		}
	}
}
