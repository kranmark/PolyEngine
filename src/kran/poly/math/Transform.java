package kran.poly.math;

import kran.poly.util.TempVars;

public class Transform implements Cloneable {
	
	static final long serialVersionUID = 1;
	
	public static final Transform IDENTITY = new Transform();
	
	private Quaternion rot = new Quaternion();
	private Vector3f translation = new Vector3f();
	private Vector3f scale = new Vector3f(1, 1, 1);
	
	public Transform(Vector3f translation, Quaternion rot) {
		this.translation.set(translation);
		this.rot.set(rot);
	}
	
	public Transform(Vector3f translation, Quaternion rot, Vector3f scale) {
		this.translation.set(translation);
		this.rot.set(rot);
		this.scale.set(scale);
	}
	
	public Transform(Vector3f translation) {
		this.translation.set(translation);
	}
	
	public Transform(Quaternion rot) {
		this.rot.set(rot);
	}
	
	public Transform() {
	}
	
	public Matrix4f getTransformMatrix(Matrix4f store) {
		if (store == null)
			store = new Matrix4f();
		store.loadIdentity();
		store.setRotationQuaternion(getRotation());
		store.setTranslation(getTranslation());
		
		TempVars vars = TempVars.get();
		Matrix4f scaleMat = vars.mat4f;
		scaleMat.loadIdentity();
		scaleMat.setScale(getScale());
		store.multLocal(scaleMat);
		vars.release();
		return store;
	}
	
	/**
	 * Sets this rotation to the given Quaternion value.
	 * 
	 * @param rot
	 *             The new rotation for this matrix.
	 * @return this
	 */
	public Transform setRotation(Quaternion rot) {
		this.rot.set(rot);
		return this;
	}
	
	/**
	 * Sets this translation to the given value.
	 * 
	 * @param trans
	 *             The new translation for this matrix.
	 * @return this
	 */
	public Transform setTranslation(Vector3f trans) {
		this.translation.set(trans);
		return this;
	}
	
	/**
	 * Return the translation vector in this matrix.
	 * 
	 * @return translation vector.
	 */
	public Vector3f getTranslation() {
		return translation;
	}
	
	/**
	 * Sets this scale to the given value.
	 * 
	 * @param scale
	 *             The new scale for this matrix.
	 * @return this
	 */
	public Transform setScale(Vector3f scale) {
		this.scale.set(scale);
		return this;
	}
	
	/**
	 * Sets this scale to the given value.
	 * 
	 * @param scale
	 *             The new scale for this matrix.
	 * @return this
	 */
	public Transform setScale(float scale) {
		this.scale.set(scale, scale, scale);
		return this;
	}
	
	/**
	 * Return the scale vector in this matrix.
	 * 
	 * @return scale vector.
	 */
	public Vector3f getScale() {
		return scale;
	}
	
	/**
	 * Stores this translation value into the given vector3f. If trans is
	 * null, a new vector3f is created to hold the value. The value, once
	 * stored, is returned.
	 * 
	 * @param trans
	 *             The store location for this matrix's translation.
	 * @return The value of this matrix's translation.
	 */
	public Vector3f getTranslation(Vector3f trans) {
		if (trans == null)
			trans = new Vector3f();
		trans.set(this.translation);
		return trans;
	}
	
	/**
	 * Stores this rotation value into the given Quaternion. If quat is
	 * null, a new Quaternion is created to hold the value. The value,
	 * once stored, is returned.
	 * 
	 * @param quat
	 *             The store location for this matrix's rotation.
	 * @return The value of this matrix's rotation.
	 */
	public Quaternion getRotation(Quaternion quat) {
		if (quat == null)
			quat = new Quaternion();
		quat.set(rot);
		return quat;
	}
	
	/**
	 * Return the rotation Quaternion in this matrix.
	 * 
	 * @return rotation Quaternion.
	 */
	public Quaternion getRotation() {
		return rot;
	}
	
	/**
	 * Stores this scale value into the given vector3f. If scale is null,
	 * a new vector3f is created to hold the value. The value, once
	 * stored, is returned.
	 * 
	 * @param scale
	 *             The store location for this matrix's scale.
	 * @return The value of this matrix's scale.
	 */
	public Vector3f getScale(Vector3f scale) {
		if (scale == null)
			scale = new Vector3f();
		scale.set(this.scale);
		return scale;
	}
	
	/**
	 * Sets this transform to the interpolation between the first transform and
	 * the second by delta amount.
	 * 
	 * @param t1
	 *             The beginning transform.
	 * @param t2
	 *             The ending transform.
	 * @param delta
	 *             An amount between 0 and 1 representing how far to
	 *             interpolate from t1 to t2.
	 */
	public void interpolateTransforms(Transform t1, Transform t2, float delta) {
		this.rot.slerp(t1.rot, t2.rot, delta);
		this.translation.interpolate(t1.translation, t2.translation, delta);
		this.scale.interpolate(t1.scale, t2.scale, delta);
	}
	
	/**
	 * Changes the values of this matrix according to it's parent. Very
	 * similar to the concept of Node/Spatial transforms.
	 * 
	 * @param parent
	 *             The parent matrix.
	 * @return This matrix, after combining.
	 */
	public Transform combineWithParent(Transform parent) {
		scale.multLocal(parent.scale);
		//	        rot.multLocal(parent.rot);
		parent.rot.mult(rot, rot);
		
		// This here, is evil code
		//	        parent
		//	            .rot
		//	            .multLocal(translation)
		//	            .multLocal(parent.scale)
		//	            .addLocal(parent.translation);
		
		translation.multLocal(parent.scale);
		parent.rot
				.multLocal(translation)
				.addLocal(parent.translation);
		return this;
	}
	
	/**
	 * Sets this matrix's translation to the given x,y,z values.
	 * 
	 * @param x
	 *             This matrix's new x translation.
	 * @param y
	 *             This matrix's new y translation.
	 * @param z
	 *             This matrix's new z translation.
	 * @return this
	 */
	public Transform setTranslation(float x, float y, float z) {
		translation.set(x, y, z);
		return this;
	}
	
	/**
	 * Sets this matrix's scale to the given x,y,z values.
	 * 
	 * @param x
	 *             This matrix's new x scale.
	 * @param y
	 *             This matrix's new y scale.
	 * @param z
	 *             This matrix's new z scale.
	 * @return this
	 */
	public Transform setScale(float x, float y, float z) {
		scale.set(x, y, z);
		return this;
	}
	
	public Vector3f transformVector(final Vector3f in, Vector3f store) {
		if (store == null)
			store = new Vector3f();
		
		// multiply with scale first, then rotate, finally translate (cf.
		// Eberly)
		return rot.mult(store.set(in).multLocal(scale), store).addLocal(translation);
	}
	
	public Vector3f transformInverseVector(final Vector3f in, Vector3f store) {
		if (store == null)
			store = new Vector3f();
		
		// The author of this code should look above and take the inverse of that
		// But for some reason, they didnt ..
		//	        in.subtract(translation, store).divideLocal(scale);
		//	        rot.inverse().mult(store, store);
		
		in.subtract(translation, store);
		rot.inverse().mult(store, store);
		store.divideLocal(scale);
		
		return store;
	}
	
	/**
	 * Loads the identity. Equal to translation=0,0,0 scale=1,1,1
	 * rot=0,0,0,1.
	 */
	public void loadIdentity() {
		translation.set(0, 0, 0);
		scale.set(1, 1, 1);
		rot.set(0, 0, 0, 1);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[ " + translation.x + ", " + translation.y + ", " + translation.z + "]\n"
				+ "[ " + rot.x + ", " + rot.y + ", " + rot.z + ", " + rot.w + "]\n"
				+ "[ " + scale.x + " , " + scale.y + ", " + scale.z + "]";
	}
	
	/**
	 * Sets this matrix to be equal to the given matrix.
	 * 
	 * @param trans
	 *             The matrix to be equal to.
	 * @return this
	 */
	public Transform set(Transform trans) {
		this.translation.set(trans.translation);
		this.rot.set(trans.rot);
		this.scale.set(trans.scale);
		return this;
	}
	
	@Override
	public Transform clone() {
		try {
			Transform tq = (Transform) super.clone();
			tq.rot = rot.clone();
			tq.scale = scale.clone();
			tq.translation = translation.clone();
			return tq;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
