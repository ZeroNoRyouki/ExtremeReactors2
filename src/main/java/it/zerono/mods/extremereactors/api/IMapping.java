/*
 *
 * IMapping.java
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

package it.zerono.mods.extremereactors.api;

import java.util.Objects;

/**
 * Maps a source to a product, with quantities attached.
 *
 * @param <Source> the source
 * @param <Product> the product
 */
public interface IMapping<Source, Product> {

    static <Source, Product> IMapping<Source, Product> of(final Source source, final int sourceAmount,
                                                          final Product product, final int productAmount) {

        if (0 == sourceAmount || 0 == productAmount) {
            return new IMapping.NoneToNone<>(source, product);
        } else if (sourceAmount == productAmount) {
            return new IMapping.OneToOne<>(source, sourceAmount, product, productAmount);
        } else {
            return new IMapping.ManyToMany<>(source, sourceAmount, product, productAmount);
        }
    }

    Source getSource();

    Product getProduct();

    int getSourceAmount();

    int getProductAmount();

    /**
     * Returns the amount of product which can be produced from a given quantity of the source thing.
     * If there is not enough of the source, returns zero.
     *
     * @param sourceQty The amount of source thing available.
     * @return The amount of product which can be produced. May be 0.
     */
    int getProductAmount(int sourceQty);

    /**
     * Returns the amount of source needed to produce a given quantity of product.
     * Note that this may not produce the full amount you requested; you should
     * check the result with getProductAmount() afterwards to see how much need be consumed.
     *
     * @param productQty The amount of product to produce
     * @return The amount of source needed to produce at most productQty units of the product.
     */
    int getSourceAmount(int productQty);

    default IMapping<Product, Source> getReverse() {
        return of(this.getProduct(), this.getProductAmount(), this.getSource(), this.getSourceAmount());
    }

    abstract class Impl<Source, Product>
            implements IMapping<Source, Product> {

        Impl(final Source source, final int sourceAmount, final Product product, final int productAmount) {

            this._source  = Objects.requireNonNull(source);
            this._product = Objects.requireNonNull(product);
            this._sourceAmount = sourceAmount;
            this._productAmount= productAmount;
        }

        //region IMapping

        public Source getSource() {
            return this._source;
        }

        public Product getProduct() {
            return this._product;
        }

        public int getSourceAmount() {
            return this._sourceAmount;
        }

        public int getProductAmount() {
            return this._productAmount;
        }

        //endregion
        //region Object

        @Override
        public String toString() {
            return String.format("%d %s to %d %s", this.getSourceAmount(), this.getSource(), this.getProductAmount(), this.getProduct());
        }

        //endregion
        //region internals

        private final Source _source;
        private final Product _product;
        private final int _sourceAmount;
        private final int _productAmount;

        //endregion
    }

    class OneToOne<Source, Product>
            extends Impl<Source, Product> {

        public OneToOne(final Source source, final int sourceAmount, final Product product, final int productAmount) {
            super(source, sourceAmount, product, productAmount);
        }

        //region IMapping

        @Override
        public int getProductAmount(final int sourceQty) {
            return sourceQty;
        }

        @Override
        public int getSourceAmount(final int productQty) {
            return productQty;
        }

        //endregion
    }

    class ManyToMany<Source, Product>
            extends Impl<Source, Product> {

        public ManyToMany(final Source source, final int sourceAmount, final Product product, final int productAmount) {
            super(source, sourceAmount, product, productAmount);
        }

        //region IMapping

        @Override
        public int getProductAmount(final int sourceQty) {
            return (sourceQty / this.getSourceAmount()) * this.getProductAmount();
        }

        @Override
        public int getSourceAmount(final int productQty) {
            return (productQty / this.getProductAmount()) * this.getSourceAmount();
        }

        //endregion
    }

    class NoneToNone<Source, Product>
            extends Impl<Source, Product> {

        public NoneToNone(final Source source, final Product product) {
            super(source, 0, product, 0);
        }

        //region IMapping

        @Override
        public int getProductAmount(final int sourceQty) {
            return 0;
        }

        @Override
        public int getSourceAmount(final int productQty) {
            return 0;
        }

        //endregion
    }
}
