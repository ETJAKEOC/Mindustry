package mindustry.entities.comp;

import static mindustry.Vars.player;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.annotations.Annotations.BaseComponent;
import mindustry.annotations.Annotations.Component;
import mindustry.annotations.Annotations.InternalImpl;
import mindustry.annotations.Annotations.MethodPriority;
import mindustry.entities.EntityGroup;
import mindustry.gen.*;

@Component
@BaseComponent
abstract class EntityComp {
	transient int id = EntityGroup.nextId();
	private transient boolean added;

	boolean isAdded() {
		return added;
	}

	void update() {
	}

	void remove() {
		added = false;
	}

	void add() {
		added = true;
	}

	boolean isLocal() {
		return ((Object) this) == player || ((Object) this) instanceof Unitc u && u.controller() == player;
	}

	boolean isRemote() {
		return ((Object) this) instanceof Unitc u && u.isPlayer() && !isLocal();
	}

	/**
	 * Replaced with `this` after code generation.
	 */
	<T extends Entityc> T self() {
		return (T) this;
	}

	<T> T as() {
		return (T) this;
	}

	@InternalImpl
	abstract int classId();

	@InternalImpl
	abstract boolean serialize();

	@MethodPriority(1)
	void read(Reads read) {
		afterRead();
	}

	void write(Writes write) {

	}

	void beforeWrite() {

	}

	void afterRead() {

	}

	//called after all entities have been read (useful for ID resolution)
	void afterReadAll() {

	}
}
