package arc.fx.filters;

import arc.fx.FxFilter;
import arc.fx.util.PingPongBuffer;
import arc.util.Disposable;

/**
 * The base class for any multi-pass filter.
 * Usually a multi-pass filter will make use of one or more single-pass filters,
 * promoting composition over inheritance.
 */
public abstract class MultipassVfxFilter implements Disposable {

	/**
	 * @see FxFilter#resize(int, int)
	 */
	public void resize(int width, int height) {

	}

	/**
	 * @see FxFilter#rebind()
	 */
	public abstract void setParams();

	public abstract void render(PingPongBuffer pingPongBuffer);
}
