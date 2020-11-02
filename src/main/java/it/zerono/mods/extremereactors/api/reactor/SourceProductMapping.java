/*
 *
 * SourceProductMapping.java
 *
 * This file is part of Extreme Reactors 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * DO NOT REMOVE OR EDIT THIS HEADER
 *
 */

package it.zerono.mods.extremereactors.api.reactor;

import java.util.Objects;

/**
 * Maps a source item to a product item, with quantities attached.
 */
@SuppressWarnings({"WeakerAccess"})
public class SourceProductMapping<S, P> {

    /**
     * Construct a new mapping
     *
     * @param source The source (item Tag, fluid Tag or reactant name)
     * @param sourceAmount The amount of source to consume to create product
     * @param product The product (item Tag, fluid Tag or reactant name)
     * @param productAmount The amount of product produced by consuming the source
     */
    SourceProductMapping(final S source, final int sourceAmount,
                         final P product, final int productAmount) {

        this._source  = Objects.requireNonNull(source);
        this._product = Objects.requireNonNull(product);
        this._sourceAmount = sourceAmount;
        this._productAmount= productAmount;
    }

    public SourceProductMapping<P, S> getReverse() {
        return new SourceProductMapping<>(this._product, this._productAmount, this._source, this._sourceAmount);
    }

    public S getSource() {
        return this._source;
    }

    public P getProduct() {
        return this._product;
    }

    public int getSourceAmount() {
        return this._sourceAmount;
    }

    public int getProductAmount() {
        return this._productAmount;
    }

    /**
     * Returns the amount of product which can be produced from a given quantity of the source thing.
     * If there is not enough of the source item, returns zero.
     *
     * @param sourceQty The amount of source thing available.
     * @return The amount of product which can be produced. May be 0.
     */
    public int getProductAmount(final int sourceQty) {
        return (sourceQty / this._sourceAmount) * this._productAmount;
    }

    /**
     * Returns the amount of source needed to produce a given quantity of product.
     * Note that this may not produce the full amount you requested; you should
     * check the result with getProductAmount() afterwards to see how much need be consumed.
     *
     * @param productQty The amount of product to produce
     * @return The amount of source needed to produce at most productQty units of the product.
     */
    public int getSourceAmount(final int productQty) {
        return (productQty / this._productAmount) * this._sourceAmount;
    }

    //region internals

    private final S _source;
    private final P _product;
    private final int _sourceAmount;
    private final int _productAmount;

    //endregion
}
